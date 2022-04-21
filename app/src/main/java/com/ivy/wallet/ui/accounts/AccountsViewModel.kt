package com.ivy.wallet.ui.accounts

import androidx.lifecycle.viewModelScope
import com.ivy.design.viewmodel.IvyViewModel
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.event.AccountsUpdatedEvent
import com.ivy.wallet.domain.fp.account.calculateAccountBalance
import com.ivy.wallet.domain.fp.account.calculateAccountIncomeExpense
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.domain.fp.exchangeToBaseCurrency
import com.ivy.wallet.domain.fp.wallet.baseCurrencyCode
import com.ivy.wallet.domain.fp.wallet.calculateWalletBalance
import com.ivy.wallet.domain.logic.AccountCreator
import com.ivy.wallet.domain.sync.item.AccountSync
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.TestIdlingResource
import com.ivy.wallet.utils.UiText
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
) : IvyViewModel<AccountState>() {
    override val mutableState: MutableStateFlow<AccountState> = MutableStateFlow(AccountState())

    @Subscribe
    fun onAccountsUpdated(event: AccountsUpdatedEvent) {
        start()
    }

    init {
        EventBus.getDefault().register(this)
    }

    fun start() {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally()
        }
    }

    private suspend fun startInternally() {
        TestIdlingResource.increment()

        val period = TimePeriod.currentMonth(
            startDayOfMonth = ivyContext.startDayOfMonth
        ) //this must be monthly
        val range = period.toRange(ivyContext.startDayOfMonth)

        val baseCurrencyCode = ioThread { baseCurrencyCode(settingsDao) }

        val accountsData = ioThread {
            accountDao.findAll()
                .map {
                    val balance = calculateAccountBalance(
                        transactionDao = walletDAOs.transactionDao,
                        accountId = it.id
                    )
                    val balanceBaseCurrency = if (it.currency != baseCurrencyCode) {
                        exchangeToBaseCurrency(
                            exchangeRateDao = walletDAOs.exchangeRateDao,
                            baseCurrencyCode = baseCurrencyCode,
                            fromCurrencyCode = it.currency ?: baseCurrencyCode,
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

        val totalBalanceWithExcluded = ioThread {
            calculateWalletBalance(
                walletDAOs = walletDAOs,
                baseCurrencyCode = baseCurrencyCode,
                filterExcluded = false
            ).value.toDouble()
        }

        updateState {
            it.copy(
                baseCurrency = baseCurrencyCode,
                accountsData = accountsData,
                totalBalanceWithExcluded = totalBalanceWithExcluded,
                totalBalanceWithExcludedText = UiText.StringResource(
                    R.string.total, baseCurrencyCode, totalBalanceWithExcluded.format(
                        baseCurrencyCode
                    )
                )
            )
        }

        TestIdlingResource.decrement()
    }

    private suspend fun reorder(newOrder: List<AccountData>) {
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
        startInternally()

        ioThread {
            accountSync.sync()
        }

        TestIdlingResource.decrement()
    }

    private suspend fun editAccount(account: Account, newBalance: Double) {
        TestIdlingResource.increment()

        accountCreator.editAccount(account, newBalance) {
            startInternally()
        }

        TestIdlingResource.decrement()
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    private suspend fun reorderModalVisible(reorderVisible: Boolean) {
        updateState {
            it.copy(reorderVisible = reorderVisible)
        }
    }

    fun onEvent(event: AccountsEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is AccountsEvent.OnReorder -> reorder(event.reorderedList)
                is AccountsEvent.OnEditAccount -> editAccount(event.editedAccount, event.newBalance)
                is AccountsEvent.OnReorderModalVisible -> reorderModalVisible(event.reorderVisible)
            }
        }
    }
}