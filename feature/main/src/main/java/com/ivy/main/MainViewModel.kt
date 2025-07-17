package com.ivy.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.SharedPrefs
import com.ivy.data.repository.CurrencyRepository
import com.ivy.domain.usecase.exchange.SyncExchangeRatesUseCase
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.model.MainTab
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.asLiveData
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase,
    private val accountCreator: AccountCreator,
    private val sharedPrefs: SharedPrefs,
    private val currencyRepository: CurrencyRepository,
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

            val baseCurrency = currencyRepository.getBaseCurrency()
            _currency.value = baseCurrency.code

            ivyContext.dataBackupCompleted =
                sharedPrefs.getBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, false)

            ioThread {
                // Sync exchange rates
                syncExchangeRatesUseCase.sync(baseCurrency)
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

            accountCreator.createAccount(data) {}

            TestIdlingResource.decrement()
        }
    }
}
