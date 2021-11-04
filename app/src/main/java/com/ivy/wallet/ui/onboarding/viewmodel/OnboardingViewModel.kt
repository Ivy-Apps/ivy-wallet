package com.ivy.wallet.ui.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.analytics.IvyAnalytics
import com.ivy.wallet.base.OpResult
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.sendToCrashlytics
import com.ivy.wallet.logic.*
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateCategoryData
import com.ivy.wallet.logic.notification.TransactionReminderLogic
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Settings
import com.ivy.wallet.network.FCMClient
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.request.auth.GoogleSignInRequest
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.sync.IvySync
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.onboarding.OnboardingState
import com.ivy.wallet.ui.onboarding.model.AccountBalance
import com.ivy.wallet.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val ivyContext: IvyContext,
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val restClient: RestClient,
    private val fcmClient: FCMClient,
    private val session: IvySession,
    private val accountLogic: WalletAccountLogic,
    private val categoryCreator: CategoryCreator,
    private val categoryDao: CategoryDao,
    private val accountCreator: AccountCreator,

    //Only OnboardingRouter stuff
    sharedPrefs: SharedPrefs,
    ivySync: IvySync,
    ivyAnalytics: IvyAnalytics,
    transactionReminderLogic: TransactionReminderLogic,
    preloadDataLogic: PreloadDataLogic,
    exchangeRatesLogic: ExchangeRatesLogic,
    logoutLogic: LogoutLogic
) : ViewModel() {

    private val _state = MutableLiveData(OnboardingState.SPLASH)
    val state = _state.asLiveData()

    private val _currency = MutableLiveData<IvyCurrency>()
    val currency = _currency.asLiveData()

    private val _opGoogleSignIn = MutableLiveData<OpResult<Unit>?>()
    val opGoogleSignIn = _opGoogleSignIn.asLiveData()

    private val _accounts = MutableLiveData<List<AccountBalance>>()
    val accounts = _accounts.asLiveData()

    private val _accountSuggestions = MutableLiveData<List<CreateAccountData>>()
    val accountSuggestions = _accountSuggestions.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _categorySuggestions = MutableLiveData<List<CreateCategoryData>>()
    val categorySuggestions = _categorySuggestions.asLiveData()

    private val router = OnboardingRouter(
        _state = _state,
        _opGoogleSignIn = _opGoogleSignIn,
        _accounts = _accounts,
        _accountSuggestions = _accountSuggestions,
        _categories = _categories,
        _categorySuggestions = _categorySuggestions,

        ivyContext = ivyContext,
        ivyAnalytics = ivyAnalytics,
        exchangeRatesLogic = exchangeRatesLogic,
        accountDao = accountDao,
        sharedPrefs = sharedPrefs,
        categoryDao = categoryDao,
        ivySync = ivySync,
        preloadDataLogic = preloadDataLogic,
        transactionReminderLogic = transactionReminderLogic,
        logoutLogic = logoutLogic
    )

    fun start(screen: Screen.Onboarding, isSystemDarkMode: Boolean) {
        viewModelScope.launch {
            initiateSettings(isSystemDarkMode)

            router.initBackHandling(
                screen = screen,
                viewModelScope = viewModelScope,
                restartOnboarding = {
                    start(screen, isSystemDarkMode)
                }
            )

            router.splashNext()
        }
    }

    private suspend fun initiateSettings(isSystemDarkMode: Boolean) {
        val defaultCurrency = IvyCurrency.getDefault()
        _currency.value = defaultCurrency

        ioThread {
            if (settingsDao.findAll().isEmpty()) {
                settingsDao.save(
                    Settings(
                        theme = if (isSystemDarkMode) Theme.DARK else Theme.LIGHT,
                        name = "",
                        currency = defaultCurrency.code,
                        bufferAmount = 1000.0
                    )
                )
            }
        }
    }

    //Step 1 ---------------------------------------------------------------------------------------
    fun loginWithGoogle() {
        ivyContext.googleSignIn { idToken ->
            if (idToken != null) {
                _opGoogleSignIn.value = OpResult.loading()
                viewModelScope.launch {
                    try {
                        loginWithGoogleOnServer(idToken)

                        router.googleLoginNext()

                        _opGoogleSignIn.value = null //reset login with Google operation state
                    } catch (e: Exception) {
                        e.sendToCrashlytics("GOOGLE_SIGN_IN ERROR: generic exception when logging with GOOGLE")
                        e.printStackTrace()
                        Timber.e("Login with Google failed on Ivy server - ${e.message}")
                        _opGoogleSignIn.value = OpResult.failure(e)
                    }
                }
            } else {
                sendToCrashlytics("GOOGLE_SIGN_IN ERROR: idToken is null!!")
                Timber.e("Login with Google failed while getting idToken")
                _opGoogleSignIn.value = OpResult.faliure("Login with Google failed, try again.")
            }
        }
    }

    private suspend fun loginWithGoogleOnServer(idToken: String) {
        val authResponse = restClient.authService.googleSignIn(
            GoogleSignInRequest(
                googleIdToken = idToken,
                fcmToken = fcmClient.fcmToken()
            )
        )

        ioThread {
            session.initiate(authResponse)

            settingsDao.save(
                settingsDao.findFirst().copy(
                    name = authResponse.user.firstName
                )
            )
        }

        _opGoogleSignIn.value = OpResult.success(Unit)
    }

    fun loginOfflineAccount() {
        viewModelScope.launch {
            router.offlineAccountNext()
        }
    }
    //Step 1 ---------------------------------------------------------------------------------------


    //Step 2 ---------------------------------------------------------------------------------------
    fun startImport() {
        router.startImport()
    }

    fun importSkip() {
        router.importSkip()
    }

    fun importFinished(success: Boolean) {
        router.importFinished(success)
    }

    fun startFresh() {
        router.startFresh()
    }
    //Step 2 ---------------------------------------------------------------------------------------


    fun setBaseCurrency(baseCurrency: IvyCurrency) {
        viewModelScope.launch {
            updateBaseCurrency(baseCurrency)

            router.setBaseCurrencyNext(
                baseCurrency = baseCurrency,
                accountsWithBalance = { accountsWithBalance() }
            )
        }
    }

    private suspend fun updateBaseCurrency(baseCurrency: IvyCurrency) {
        ioThread {
            settingsDao.save(
                settingsDao.findFirst().copy(
                    currency = baseCurrency.code
                )
            )
        }
        _currency.value = baseCurrency
    }

    //--------------------- Accounts ---------------------------------------------------------------
    fun editAccount(account: Account, newBalance: Double) {
        viewModelScope.launch {
            accountCreator.editAccount(account, newBalance) {
                _accounts.value = accountsWithBalance()
            }
        }
    }


    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                _accounts.value = accountsWithBalance()
            }
        }
    }

    private suspend fun accountsWithBalance(): List<AccountBalance> = ioThread {
        accountDao.findAll()
            .map {
                AccountBalance(
                    account = it,
                    balance = accountLogic.calculateAccountBalance(it)
                )
            }
    }

    fun onAddAccountsDone() {
        viewModelScope.launch {
            router.accountsNext()
        }
    }

    fun onAddAccountsSkip() {
        viewModelScope.launch {
            router.accountsSkip()
        }
    }
    //--------------------- Accounts ---------------------------------------------------------------

    //---------------------------- Categories ------------------------------------------------------
    fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                _categories.value = ioThread { categoryDao.findAll() }!!
            }
        }
    }

    fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                _categories.value = ioThread { categoryDao.findAll() }!!
            }
        }
    }

    fun onAddCategoriesDone() {
        viewModelScope.launch {
            router.categoriesNext(baseCurrency = currency.value)
        }
    }

    fun onAddCategoriesSkip() {
        viewModelScope.launch {
            router.categoriesSkip(baseCurrency = currency.value)
        }
    }
    //---------------------------- Categories ------------------------------------------------------
}