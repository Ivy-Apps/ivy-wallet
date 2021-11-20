package com.ivy.wallet.ui.accounts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.logic.WalletAccountLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.sync.item.AccountSync
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val walletLogic: WalletLogic,
    private val accountLogic: WalletAccountLogic,
    private val accountSync: AccountSync,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountCreator: AccountCreator,
    private val ivyContext: IvyContext,
) : ViewModel() {

    @Subscribe
    fun onAccountsUpdated(event: AccountsUpdatedEvent) {
        start()
    }

    init {
        EventBus.getDefault().register(this)
    }

    private val _baseCurrency = MutableLiveData<String>()
    val baseCurrency = _baseCurrency.asLiveData()

    private val _accounts = MutableLiveData<List<AccountData>>()
    val accounts = _accounts.asLiveData()

    private val _totalBalanceWithExcluded = MutableLiveData<Double>()
    val totalBalanceWithExcluded = _totalBalanceWithExcluded.asLiveData()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val period = TimePeriod.currentMonth(
                startDayOfMonth = ivyContext.startDayOfMonth
            ) //this must be monthly
            val range = period.toRange(ivyContext.startDayOfMonth)

            val baseCurrency = ioThread { settingsDao.findFirst().currency }
            _baseCurrency.value = baseCurrency

            _accounts.value = ioThread {
                accountDao
                    .findAll()
                    .map {
                        val balance = accountLogic.calculateAccountBalance(it)
                        val balanceBaseCurrency = if (it.currency != baseCurrency) {
                            exchangeRatesLogic.amountBaseCurrency(
                                amount = balance,
                                amountCurrency = it.currency ?: baseCurrency,
                                baseCurrency = baseCurrency
                            )
                        } else {
                            null
                        }

                        AccountData(
                            account = it,
                            balance = balance,
                            balanceBaseCurrency = balanceBaseCurrency,
                            monthlyIncome = accountLogic.calculateAccountIncome(
                                account = it,
                                range = range
                            ),
                            monthlyExpenses = accountLogic.calculateAccountExpenses(
                                account = it,
                                range = range
                            ),
                        )
                    }
            }!!

            _totalBalanceWithExcluded.value = ioThread {
                walletLogic.calculateBalance(filterExcluded = false)
            }!!

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