package com.ivy.wallet.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.toOption
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.readOnly
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.functional.account.calculateAccountBalance
import com.ivy.wallet.functional.account.calculateAccountIncomeExpense
import com.ivy.wallet.functional.data.WalletDAOs
import com.ivy.wallet.functional.exchangeToBaseCurrency
import com.ivy.wallet.functional.wallet.baseCurrencyCode
import com.ivy.wallet.functional.wallet.calculateWalletBalance
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.sync.item.AccountSync
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val accountSync: AccountSync,
    private val accountCreator: AccountCreator,
    private val ivyContext: IvyWalletCtx,
) : ViewModel() {

    @Subscribe
    fun onAccountsUpdated(event: AccountsUpdatedEvent) {
        start()
    }

    init {
        EventBus.getDefault().register(this)
    }

    private val _baseCurrencyCode = MutableStateFlow("")
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _accounts = MutableStateFlow<List<AccountData>>(emptyList())
    val accounts = _accounts.readOnly()

    private val _totalBalanceWithExcluded = MutableStateFlow(0.0)
    val totalBalanceWithExcluded = _totalBalanceWithExcluded.readOnly()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val period = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ) //this must be monthly
            val range = period.toRange(ivyContext.startDayOfMonth)

            val baseCurrencyCode = ioThread { baseCurrencyCode(settingsDao) }
            _baseCurrencyCode.value = baseCurrencyCode

            _accounts.value = ioThread {
                accountDao.findAll()
                    .map {
                        val balance = calculateAccountBalance(
                            transactionDao = walletDAOs.transactionDao,
                            accountId = it.id
                        )
                        val balanceBaseCurrency = if (it.currency != baseCurrencyCode) {
                            exchangeToBaseCurrency(
                                exchangeRateDao = walletDAOs.exchangeRateDao,
                                baseCurrencyCode = baseCurrencyCode.toOption(),
                                fromCurrencyCode = (it.currency ?: baseCurrencyCode).toOption(),
                                fromAmount = balance
                            ).orNull()?.toDouble()
                        } else {
                            null
                        }

                        val incomeExpensePair = calculateAccountIncomeExpense(
                            transactionDao = walletDAOs.transactionDao,
                            accountId = it.id,
                            range = range.toCloseTimeRange()
                        )

                        AccountData(
                            account = it,
                            balance = balance.toDouble(),
                            balanceBaseCurrency = balanceBaseCurrency,
                            monthlyIncome = incomeExpensePair.income.toDouble(),
                            monthlyExpenses = incomeExpensePair.expense.toDouble(),
                        )
                    }
            }

            _totalBalanceWithExcluded.value = ioThread {
                calculateWalletBalance(
                    walletDAOs = walletDAOs,
                    baseCurrencyCode = baseCurrencyCode,
                    filterExcluded = false
                ).value.toDouble()
            }

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<AccountData>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.mapIndexed { index, accountData ->
                    accountDao.save(
                        accountData.account.copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            ioThread {
                accountSync.sync()
            }

            TestIdlingResource.decrement()
        }
    }

    fun editAccount(account: Account, newBalance: Double) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.editAccount(account, newBalance) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

}