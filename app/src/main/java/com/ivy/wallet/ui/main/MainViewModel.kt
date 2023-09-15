package com.ivy.wallet.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.legacy.data.SharedPrefs
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.model.MainTab
import com.ivy.legacy.utils.asLiveData
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.event.AccountsUpdatedEvent
import com.ivy.core.data.db.dao.SettingsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val accountCreator: AccountCreator,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    fun start(screen: MainScreen) {
        nav.onBackPressed[screen] = {
            if (ivyContext.mainTab == MainTab.ACCOUNTS) {
                ivyContext.selectMainTab(MainTab.HOME)
                true
            } else {
                // Exiting (the backstack will close the app)
                false
            }
        }

        viewModelScope.launch {
            TestIdlingResource.increment()

            val baseCurrency = ioThread { settingsDao.findFirst().currency }
            _currency.value = baseCurrency

            ivyContext.dataBackupCompleted =
                sharedPrefs.getBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, false)

            ioThread {
                // Sync exchange rates
                syncExchangeRatesAct(
                    SyncExchangeRatesAct.Input(baseCurrency = baseCurrency)
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun selectTab(tab: MainTab) {
        ivyContext.selectMainTab(tab)
    }

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                EventBus.getDefault().post(AccountsUpdatedEvent())
            }

            TestIdlingResource.decrement()
        }
    }
}
