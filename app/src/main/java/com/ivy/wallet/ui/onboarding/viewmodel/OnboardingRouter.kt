package com.ivy.wallet.ui.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.LogoutLogic
import com.ivy.wallet.domain.deprecated.logic.PreloadDataLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.ui.Import
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.Onboarding
import com.ivy.wallet.ui.onboarding.OnboardingState
import com.ivy.wallet.ui.onboarding.model.AccountBalance
import com.ivy.wallet.utils.OpResult
import com.ivy.wallet.utils.ioThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingRouter(
    private val _opGoogleSignIn: MutableLiveData<OpResult<Unit>?>,
    private val _state: MutableLiveData<OnboardingState>,
    private val _accounts: MutableLiveData<List<AccountBalance>>,
    private val _accountSuggestions: MutableLiveData<List<CreateAccountData>>,
    private val _categories: MutableLiveData<List<Category>>,
    private val _categorySuggestions: MutableLiveData<List<CreateCategoryData>>,

    private val nav: Navigation,
    private val accountDao: AccountDao,
    private val sharedPrefs: SharedPrefs,
    private val transactionReminderLogic: TransactionReminderLogic,
    private val preloadDataLogic: PreloadDataLogic,
    private val categoryDao: CategoryDao,
    private val logoutLogic: LogoutLogic,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
) {

    var isLoginCache = false

    fun initBackHandling(
        screen: Onboarding,
        viewModelScope: CoroutineScope,
        restartOnboarding: () -> Unit
    ) {
        nav.onBackPressed[screen] = {
            when (_state.value) {
                OnboardingState.SPLASH -> {
                    // do nothing, consume back
                    true
                }
                OnboardingState.LOGIN -> {
                    // let the user exit the app
                    false
                }
                OnboardingState.CHOOSE_PATH -> {
                    _state.value = OnboardingState.LOGIN
                    true
                }
                OnboardingState.CURRENCY -> {
                    if (isLoginCache) {
                        // user with Ivy account
                        viewModelScope.launch {
                            logoutLogic.logout()
                            isLoginCache = false
                            restartOnboarding()
                            _state.value = OnboardingState.LOGIN
                        }
                    } else {
                        // fresh user
                        _state.value = OnboardingState.CHOOSE_PATH
                    }
                    true
                }
                OnboardingState.ACCOUNTS -> {
                    _state.value = OnboardingState.CURRENCY
                    true
                }
                OnboardingState.CATEGORIES -> {
                    _state.value = OnboardingState.ACCOUNTS
                    true
                }
                null -> {
                    // do nothing, consume back
                    true
                }
            }
        }
    }

    // ------------------------------------- Step 0 - Splash ----------------------------------------
    suspend fun splashNext() {
        if (_state.value == OnboardingState.SPLASH) {
            delay(1000)

            _state.value = OnboardingState.LOGIN
        }
    }
    // ------------------------------------- Step 0 -------------------------------------------------

    // ------------------------------------- Step 1 - Login -----------------------------------------
    suspend fun googleLoginNext() {
        if (isLogin()) {
            // Route logged user
            _state.value = OnboardingState.CURRENCY
        } else {
            // Route new user
            _state.value = OnboardingState.CHOOSE_PATH
        }
    }

    private suspend fun isLogin(): Boolean {
        isLoginCache = ioThread { accountDao.findAll().isNotEmpty() }
        return isLoginCache
    }

    suspend fun offlineAccountNext() {
        _state.value = OnboardingState.CHOOSE_PATH
    }
    // ------------------------------------- Step 1 -------------------------------------------------

    // ------------------------------------- Step 2 - Choose path -----------------------------------
    fun startImport() {
        nav.navigateTo(
            Import(
                launchedFromOnboarding = true
            )
        )
    }

    fun importSkip() {
        _state.value = OnboardingState.CURRENCY
    }

    fun importFinished(success: Boolean) {
        if (success) {
            _state.value = OnboardingState.CURRENCY
        }
    }

    fun startFresh() {
        _state.value = OnboardingState.CURRENCY
    }
    // ------------------------------------- Step 2 -------------------------------------------------

    // ------------------------------------- Step 3 - Currency --------------------------------------
    suspend fun setBaseCurrencyNext(
        baseCurrency: IvyCurrency,
        accountsWithBalance: suspend () -> List<AccountBalance>,
    ) {
        routeToAccounts(
            baseCurrency = baseCurrency,
            accountsWithBalance = accountsWithBalance
        )

        if (isLogin()) {
            completeOnboarding(baseCurrency = baseCurrency)
        }
    }
    // ------------------------------------- Step 3 -------------------------------------------------

    // ------------------------------------- Step 4 - Accounts --------------------------------------
    suspend fun accountsNext() {
        routeToCategories()
    }

    suspend fun accountsSkip() {
        routeToCategories()

        ioThread {
            preloadDataLogic.preloadAccounts()
        }
    }
    // ------------------------------------- Step 4 -------------------------------------------------

    // ------------------------------------- Step 5 - Categories ------------------------------------
    suspend fun categoriesNext(baseCurrency: IvyCurrency?) {
        completeOnboarding(baseCurrency = baseCurrency)
    }

    suspend fun categoriesSkip(baseCurrency: IvyCurrency?) {
        completeOnboarding(baseCurrency = baseCurrency)

        ioThread {
            preloadDataLogic.preloadCategories()
        }
    }
    // ------------------------------------- Step 5 -------------------------------------------------

    // -------------------------------------- Routes ------------------------------------------------
    private suspend fun routeToAccounts(
        baseCurrency: IvyCurrency,
        accountsWithBalance: suspend () -> List<AccountBalance>,
    ) {
        val accounts = accountsWithBalance()
        _accounts.value = accounts

        _accountSuggestions.value =
            preloadDataLogic.accountSuggestions(baseCurrency.code)
        _state.value = OnboardingState.ACCOUNTS
    }

    private suspend fun routeToCategories() {
        _categories.value = ioThread { categoryDao.findAll().map { it.toDomain() } }!!
        _categorySuggestions.value = preloadDataLogic.categorySuggestions()

        _state.value = OnboardingState.CATEGORIES
    }

    private suspend fun completeOnboarding(
        baseCurrency: IvyCurrency?
    ) {
        sharedPrefs.putBoolean(SharedPrefs.ONBOARDING_COMPLETED, true)

        navigateOutOfOnboarding()

        // the rest below is not UI stuff so I don't care
        ioThread {
            transactionReminderLogic.scheduleReminder()

            syncExchangeRatesAct(
                SyncExchangeRatesAct.Input(
                    baseCurrency = baseCurrency?.code ?: IvyCurrency.getDefault().code
                )
            )
        }

        resetState()
    }

    private fun resetState() {
        _state.value = OnboardingState.SPLASH
        _opGoogleSignIn.value = null
    }

    private fun navigateOutOfOnboarding() {
        nav.resetBackStack()
        nav.navigateTo(Main)
    }
    // -------------------------------------- Routes ------------------------------------------------
}
