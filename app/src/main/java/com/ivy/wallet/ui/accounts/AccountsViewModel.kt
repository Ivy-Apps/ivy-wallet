package com.ivy.wallet.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.viewmodel.account.AccountDataAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.sync.item.AccountSync
import com.ivy.wallet.domain.event.AccountsUpdatedEvent
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.TestIdlingResource
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
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
    private val accountsAct: AccountsAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val accountDataAct: AccountDataAct
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

            val baseCurrencyCode = baseCurrencyAct(Unit)
            _baseCurrencyCode.value = baseCurrencyCode

            val accs = accountsAct(Unit)

            _accounts.value = accountDataAct(
                AccountDataAct.Input(
                    accounts = accs,
                    range = range.toCloseTimeRange(),
                    baseCurrency = baseCurrencyCode
                )
            )

            _totalBalanceWithExcluded.value = calcWalletBalanceAct(
                CalcWalletBalanceAct.Input(
                    baseCurrency = baseCurrencyCode,
                    withExcluded = true
                )
            ).toDouble()

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<AccountData>) {
        viewModelScope.launch {
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