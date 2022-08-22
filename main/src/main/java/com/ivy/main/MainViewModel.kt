package com.ivy.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.screens.Main
import com.ivy.temp.event.AccountsUpdatedEvent
import com.ivy.wallet.domain.action.settings.BaseCurrencyActOld
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.sync.IvySync
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val nav: Navigation,
    private val ivySync: IvySync,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountCreator: AccountCreator,
    private val baseCurrencyAct: BaseCurrencyActOld
) : ViewModel() {

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    fun start(screen: Main) {
        nav.onBackPressed[screen] = {
            if (ivyContext.mainTab == com.ivy.base.MainTab.ACCOUNTS) {
                ivyContext.selectMainTab(com.ivy.base.MainTab.HOME)
                true
            } else {
                //Exiting (the backstack will close the app)
                false
            }
        }

        viewModelScope.launch {
            TestIdlingResource.increment()

            val baseCurrency = baseCurrencyAct(Unit)
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

    fun selectTab(tab: com.ivy.base.MainTab) {
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