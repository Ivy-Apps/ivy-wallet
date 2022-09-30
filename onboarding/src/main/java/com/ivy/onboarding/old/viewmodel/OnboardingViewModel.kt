//package com.ivy.onboarding.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.ivy.base.AccountBalance
//import com.ivy.core.ui.temp.trash.IvyWalletCtx
//import com.ivy.data.*
//import com.ivy.frp.test.TestIdlingResource
//
//import com.ivy.onboarding.OnboardingState
//import com.ivy.wallet.domain.action.account.AccountsActOld
//import com.ivy.wallet.domain.action.category.CategoriesActOld
//import com.ivy.wallet.domain.deprecated.logic.*
//import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
//import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
//import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
//import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
//import com.ivy.wallet.domain.deprecated.sync.IvySync
//import com.ivy.wallet.io.network.IvySession
//import com.ivy.wallet.io.network.RestClient
//import com.ivy.wallet.io.network.request.auth.GoogleSignInRequest
//import com.ivy.wallet.io.persistence.SharedPrefs
//import com.ivy.wallet.io.persistence.dao.AccountDao
//import com.ivy.wallet.io.persistence.dao.CategoryDao
//import com.ivy.wallet.io.persistence.dao.SettingsDao
//import com.ivy.wallet.io.persistence.data.toEntity
//import com.ivy.wallet.utils.OpResult
//import com.ivy.wallet.utils.asLiveData
//import com.ivy.wallet.utils.ioThread
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import javax.inject.Inject
//
//@HiltViewModel
//class OnboardingViewModel @Inject constructor(
//    private val ivyContext: IvyWalletCtx,
//    private val
//    private val accountDao: AccountDao,
//    private val settingsDao: SettingsDao,
//    private val restClient: RestClient,
//    private val session: IvySession,
//    private val accountLogic: WalletAccountLogic,
//    private val categoryCreator: CategoryCreator,
//    private val categoryDao: CategoryDao,
//    private val accountCreator: AccountCreator,
//
//    private val accountsAct: AccountsActOld,
//    private val categoriesAct: CategoriesActOld,
//
//    //Only OnboardingRouter stuff
//    sharedPrefs: SharedPrefs,
//    ivySync: IvySync,
//    transactionReminderLogic: TransactionReminderLogic,
//    preloadDataLogic: PreloadDataLogic,
//    exchangeRatesLogic: ExchangeRatesLogic,
//    logoutLogic: LogoutLogic
//) : ViewModel() {
//
//    private val _state = MutableLiveData(OnboardingState.SPLASH)
//    val state = _state.asLiveData()
//
//    private val _currency = MutableLiveData<IvyCurrency>()
//    val currency = _currency.asLiveData()
//
//    private val _opGoogleSignIn = MutableLiveData<OpResult<Unit>?>()
//    val opGoogleSignIn = _opGoogleSignIn.asLiveData()
//
//    private val _accounts = MutableLiveData<List<AccountBalance>>()
//    val accounts = _accounts.asLiveData()
//
//    private val _accountSuggestions = MutableLiveData<List<CreateAccountData>>()
//    val accountSuggestions = _accountSuggestions.asLiveData()
//
//    private val _categories = MutableLiveData<List<CategoryOld>>()
//    val categories = _categories.asLiveData()
//
//    private val _categorySuggestions = MutableLiveData<List<CreateCategoryData>>()
//    val categorySuggestions = _categorySuggestions.asLiveData()
//
//    private val router = OnboardingRouter(
//        _state = _state,
//        _opGoogleSignIn = _opGoogleSignIn,
//        _accounts = _accounts,
//        _accountSuggestions = _accountSuggestions,
//        _categories = _categories,
//        _categorySuggestions = _categorySuggestions,
//
//        ivyContext = ivyContext,
//
//        exchangeRatesLogic = exchangeRatesLogic,
//        accountDao = accountDao,
//        sharedPrefs = sharedPrefs,
//        categoryDao = categoryDao,
//        ivySync = ivySync,
//        preloadDataLogic = preloadDataLogic,
//        transactionReminderLogic = transactionReminderLogic,
//        logoutLogic = logoutLogic
//    )
//
//    fun start(isSystemDarkMode: Boolean) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            initiateSettings(isSystemDarkMode)
//
////            router.initBackHandling(
////                screen = screen,
////                viewModelScope = viewModelScope,
////                restartOnboarding = {
////                    start(isSystemDarkMode)
////                }
////            )
//
//            router.splashNext()
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    private suspend fun initiateSettings(isSystemDarkMode: Boolean) {
//        val defaultCurrency = IvyCurrency.getDefault()
//        _currency.value = defaultCurrency
//
//        ioThread {
//            TestIdlingResource.increment()
//
//            if (settingsDao.findAll().isEmpty()) {
//                settingsDao.save(
//                    Settings(
//                        theme = if (isSystemDarkMode) Theme.DARK else Theme.LIGHT,
//                        name = "",
//                        baseCurrency = defaultCurrency.code,
//                        bufferAmount = 1000.0.toBigDecimal()
//                    ).toEntity()
//                )
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    //Step 1 ---------------------------------------------------------------------------------------
//    fun loginWithGoogle() {
//        ivyContext.googleSignIn { idToken ->
//            if (idToken != null) {
//                _opGoogleSignIn.value = OpResult.loading()
//                viewModelScope.launch {
//                    TestIdlingResource.increment()
//
//                    try {
//                        loginWithGoogleOnServer(idToken)
//
//                        router.googleLoginNext()
//
//                        _opGoogleSignIn.value = null //reset login with Google operation state
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Timber.e("Login with Google failed on Ivy server - ${e.message}")
//                        _opGoogleSignIn.value = OpResult.failure(e)
//                    }
//
//                    TestIdlingResource.decrement()
//                }
//            } else {
//                Timber.e("Login with Google failed while getting idToken")
//                _opGoogleSignIn.value = OpResult.faliure("Login with Google failed, try again.")
//            }
//        }
//    }
//
//    private suspend fun loginWithGoogleOnServer(idToken: String) {
//        TestIdlingResource.increment()
//
//        val authResponse = restClient.authService.googleSignIn(
//            GoogleSignInRequest(
//                googleIdToken = idToken,
//                fcmToken = "n/a"
//            )
//        )
//
//        ioThread {
//            session.initiate(authResponse)
//
//            settingsDao.save(
//                settingsDao.findFirstSuspend().copy(
//                    name = authResponse.user.firstName
//                )
//            )
//        }
//
//        _opGoogleSignIn.value = OpResult.success(Unit)
//
//        TestIdlingResource.decrement()
//    }
//
//    fun loginOfflineAccount() {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//            router.offlineAccountNext()
//            TestIdlingResource.decrement()
//        }
//    }
//    //Step 1 ---------------------------------------------------------------------------------------
//
//
//    //Step 2 ---------------------------------------------------------------------------------------
//    fun startImport() {
//        router.startImport()
//    }
//
//    fun importSkip() {
//        router.importSkip()
//    }
//
//    fun importFinished(success: Boolean) {
//        router.importFinished(success)
//    }
//
//    fun startFresh() {
//        router.startFresh()
//    }
//    //Step 2 ---------------------------------------------------------------------------------------
//
//
//    fun setBaseCurrency(baseCurrency: IvyCurrency) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            updateBaseCurrency(baseCurrency)
//
//            router.setBaseCurrencyNext(
//                baseCurrency = baseCurrency,
//                accountsWithBalance = { accountsWithBalance() }
//            )
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    private suspend fun updateBaseCurrency(baseCurrency: IvyCurrency) {
//        ioThread {
//            TestIdlingResource.increment()
//
//            settingsDao.save(
//                settingsDao.findFirstSuspend().copy(
//                    currency = baseCurrency.code
//                )
//            )
//
//            TestIdlingResource.decrement()
//        }
//        _currency.value = baseCurrency
//    }
//
//    //--------------------- Accounts ---------------------------------------------------------------
//    fun editAccount(account: AccountOld, newBalance: Double) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            accountCreator.editAccount(account, newBalance) {
//                _accounts.value = accountsWithBalance()
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//
//    fun createAccount(data: CreateAccountData) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            accountCreator.createAccount(data) {
//                _accounts.value = accountsWithBalance()
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    private suspend fun accountsWithBalance(): List<AccountBalance> = ioThread {
//        accountsAct(Unit)
//            .map {
//                AccountBalance(
//                    account = it,
//                    balance = ioThread { accountLogic.calculateAccountBalance(it) }
//                )
//            }
//    }
//
//    fun onAddAccountsDone() {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            router.accountsNext()
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    fun onAddAccountsSkip() {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            router.accountsSkip()
//
//            TestIdlingResource.decrement()
//        }
//    }
//    //--------------------- Accounts ---------------------------------------------------------------
//
//    //---------------------------- Categories ------------------------------------------------------
//    fun editCategory(updatedCategory: CategoryOld) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            categoryCreator.editCategory(updatedCategory) {
//                _categories.value = categoriesAct(Unit)!!
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    fun createCategory(data: CreateCategoryData) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            categoryCreator.createCategory(data) {
//                _categories.value = categoriesAct(Unit)!!
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    fun onAddCategoriesDone() {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            router.categoriesNext(baseCurrency = currency.value)
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    fun onAddCategoriesSkip() {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            router.categoriesSkip(baseCurrency = currency.value)
//
//            TestIdlingResource.decrement()
//        }
//    }
//    //---------------------------- Categories ------------------------------------------------------
//}