package com.ivy.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.domain.event.AccountUpdatedEvent
import com.ivy.domain.event.EventBus
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.MainTab
import com.ivy.legacy.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.asLiveData
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val accountCreator: AccountCreator,
    private val sharedPrefs: SharedPrefs,
    private val eventBus: EventBus,
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
                eventBus.post(AccountUpdatedEvent)
            }

            TestIdlingResource.decrement()
        }
    }
}
