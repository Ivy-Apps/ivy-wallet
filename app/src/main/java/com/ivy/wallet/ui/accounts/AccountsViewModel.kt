package com.ivy.wallet.ui.accounts

import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.viewmodel.account.AccountDataAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.sync.item.AccountSync
import com.ivy.wallet.domain.event.AccountsUpdatedEvent
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
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
    private val sharedPrefs: SharedPrefs,
    private val accountsAct: AccountsAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountDataAct: AccountDataAct
) : FRPViewModel<AccountState, Unit>() {
    override val _state: MutableStateFlow<AccountState> = MutableStateFlow(AccountState())

    override suspend fun handleEvent(event: Unit): suspend () -> AccountState {
        TODO("Not yet implemented")
    }

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

        val baseCurrencyCode = baseCurrencyAct(Unit)
        val accs = accountsAct(Unit)

        val includeTransfersInCalc =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

        val accountsDataList = accountDataAct(
            AccountDataAct.Input(
                accounts = accs,
                range = range.toCloseTimeRange(),
                baseCurrency = baseCurrencyCode,
                includeTransfersInCalc = includeTransfersInCalc
            )
        )

        val totalBalanceWithExcluded = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(
                baseCurrency = baseCurrencyCode,
                withExcluded = true
            )
        ).toDouble()

        updateState {
            it.copy(
                baseCurrency = baseCurrencyCode,
                accountsData = accountsDataList,
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
                    accountData.account.toEntity().copy(
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