package com.ivy.wallet.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.logic.bankintegrations.BankIntegrationsLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.sync.IvySync
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val ivySync: IvySync,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountCreator: AccountCreator,
    private val bankIntegrationsLogic: BankIntegrationsLogic
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    fun start(screen: Screen.Main) {
        ivyContext.onBackPressed[screen] = {
            if (ivyContext.mainTab == MainTab.ACCOUNTS) {
                ivyContext.selectMainTab(MainTab.HOME)
                true
            } else {
                //Exiting (the backstack will close the app)
                false
            }
        }

        viewModelScope.launch {
            TestIdlingResource.increment()

            val baseCurrency = ioThread { settingsDao.findFirst().currency }
            _currency.value = baseCurrency

            ioThread {
//                try {
//                    bankIntegrationsLogic.sync() //sync bank integrations
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
                ivySync.sync() //sync app data

                //Sync exchange rates
                exchangeRatesLogic.sync(
                    baseCurrency = baseCurrency
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