package com.ivy.onboarding.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Theme
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.domain.ComposeViewModel
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.LogoutLogic
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.AccountBalance
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.Settings
import com.ivy.legacy.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.OpResult
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.sendToCrashlytics
import com.ivy.navigation.Navigation
import com.ivy.navigation.OnboardingScreen
import com.ivy.onboarding.OnboardingDetailState
import com.ivy.onboarding.OnboardingEvent
import com.ivy.onboarding.OnboardingState
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.PreloadDataLogic
import com.ivy.wallet.domain.deprecated.logic.WalletAccountLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val accountLogic: WalletAccountLogic,
    private val categoryCreator: CategoryCreator,
    private val categoryDao: CategoryDao,
    private val accountCreator: AccountCreator,

    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val settingsWriter: WriteSettingsDao,

    // Only OnboardingRouter stuff
    sharedPrefs: SharedPrefs,
    transactionReminderLogic: TransactionReminderLogic,
    preloadDataLogic: PreloadDataLogic,
    logoutLogic: LogoutLogic,
) : ComposeViewModel<OnboardingDetailState, OnboardingEvent>() {

    private val _state = mutableStateOf(OnboardingState.SPLASH)
    val state: State<OnboardingState> = _state

    private val _currency = mutableStateOf(IvyCurrency.getDefault())
    private val _opGoogleSignIn = mutableStateOf<OpResult<Unit>?>(null)
    private val _accounts = mutableStateOf(listOf<AccountBalance>().toImmutableList())
    private val _accountSuggestions = mutableStateOf(listOf<CreateAccountData>().toImmutableList())
    private val _categories = mutableStateOf(listOf<Category>().toImmutableList())
    private val _categorySuggestions =
        mutableStateOf(listOf<CreateCategoryData>().toImmutableList())

    @Composable
    override fun uiState(): OnboardingDetailState {
        return OnboardingDetailState(
            currency = _currency.value,
            opGoogleSignIn = _opGoogleSignIn.value,
            accounts = _accounts.value,
            accountSuggestions = _accountSuggestions.value,
            categories = _categories.value,
            categorySuggestions = _categorySuggestions.value
        )
    }

    private val router = OnboardingRouter(
        _state = _state,
        _opGoogleSignIn = _opGoogleSignIn,
        _accounts = _accounts,
        _accountSuggestions = _accountSuggestions,
        _categories = _categories,
        _categorySuggestions = _categorySuggestions,

        nav = nav,
        accountDao = accountDao,
        sharedPrefs = sharedPrefs,
        categoryDao = categoryDao,
        preloadDataLogic = preloadDataLogic,
        transactionReminderLogic = transactionReminderLogic,
        logoutLogic = logoutLogic,
        syncExchangeRatesAct = syncExchangeRatesAct,
    )

    fun start(screen: OnboardingScreen, isSystemDarkMode: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            initiateSettings(isSystemDarkMode)

            router.initBackHandling(
                screen = screen,
                viewModelScope = viewModelScope,
                restartOnboarding = {
                    start(screen, isSystemDarkMode)
                }
            )

            router.splashNext()

            TestIdlingResource.decrement()
        }
    }

    private suspend fun initiateSettings(isSystemDarkMode: Boolean) {
        val defaultCurrency = IvyCurrency.getDefault()
        _currency.value = defaultCurrency

        ioThread {
            TestIdlingResource.increment()

            if (settingsDao.findAll().isEmpty()) {
                settingsWriter.save(
                    Settings(
                        theme = if (isSystemDarkMode) Theme.DARK else Theme.LIGHT,
                        name = "",
                        baseCurrency = defaultCurrency.code,
                        bufferAmount = 1000.0.toBigDecimal()
                    ).toEntity()
                )
            }

            TestIdlingResource.decrement()
        }
    }

    override fun onEvent(event: OnboardingEvent) {
        viewModelScope.launch {
            when (event) {
                is OnboardingEvent.createAccount -> createAccount(event.data)
                is OnboardingEvent.createCategory -> createCategory(event.data)
                is OnboardingEvent.editAccount -> editAccount(event.account, event.newBalance)
                is OnboardingEvent.editCategory -> editCategory(event.updatedCategory)
                is OnboardingEvent.importFinished -> importFinished(event.success)
                OnboardingEvent.importSkip -> importSkip()
                OnboardingEvent.loginOfflineAccount -> loginOfflineAccount()
                OnboardingEvent.loginWithGoogle -> loginWithGoogle()
                OnboardingEvent.onAddAccountsDone -> onAddAccountsDone()
                OnboardingEvent.onAddAccountsSkip -> onAddAccountsSkip()
                OnboardingEvent.onAddCategoriesDone -> onAddCategoriesDone()
                OnboardingEvent.onAddCategoriesSkip -> onAddCategoriesSkip()
                is OnboardingEvent.setBaseCurrency -> setBaseCurrency(event.baseCurrency)
                OnboardingEvent.startFresh -> startFresh()
                OnboardingEvent.startImport -> startImport()
            }
        }
    }

    // Step 1 ---------------------------------------------------------------------------------------
    private suspend fun loginWithGoogle() {
        ivyContext.googleSignIn { idToken ->
            if (idToken != null) {
                _opGoogleSignIn.value = OpResult.loading()
                viewModelScope.launch {
                    TestIdlingResource.increment()

                    try {
                        loginWithGoogleOnServer(idToken)

                        router.googleLoginNext()

                        _opGoogleSignIn.value = null // reset login with Google operation state
                    } catch (e: Exception) {
                        e.sendToCrashlytics("GOOGLE_SIGN_IN ERROR: generic exception when logging with GOOGLE")
                        e.printStackTrace()
                        Timber.e("Login with Google failed on Ivy server - ${e.message}")
                        _opGoogleSignIn.value = OpResult.failure(e)
                    }

                    TestIdlingResource.decrement()
                }
            } else {
                sendToCrashlytics("GOOGLE_SIGN_IN ERROR: idToken is null!!")
                Timber.e("Login with Google failed while getting idToken")
                _opGoogleSignIn.value = OpResult.faliure("Login with Google failed, try again.")
            }
        }
    }

    private suspend fun loginWithGoogleOnServer(idToken: String) {
        TestIdlingResource.increment()

        TestIdlingResource.decrement()
    }

    private suspend fun loginOfflineAccount() {
        TestIdlingResource.increment()
        router.offlineAccountNext()
        TestIdlingResource.decrement()
    }
    // Step 1 ---------------------------------------------------------------------------------------

    // Step 2 ---------------------------------------------------------------------------------------
    private fun startImport() {
        router.startImport()
    }

    fun importSkip() {
        router.importSkip()
    }

    fun importFinished(success: Boolean) {
        router.importFinished(success)
    }

    private fun startFresh() {
        router.startFresh()
    }
    // Step 2 ---------------------------------------------------------------------------------------

    private suspend fun setBaseCurrency(baseCurrency: IvyCurrency) {
        TestIdlingResource.increment()

        updateBaseCurrency(baseCurrency)

        router.setBaseCurrencyNext(
            baseCurrency = baseCurrency,
            accountsWithBalance = { accountsWithBalance() }
        )

        TestIdlingResource.decrement()
    }

    private suspend fun updateBaseCurrency(baseCurrency: IvyCurrency) {
        ioThread {
            TestIdlingResource.increment()

            settingsWriter.save(
                settingsDao.findFirst().copy(
                    currency = baseCurrency.code
                )
            )

            TestIdlingResource.decrement()
        }
        _currency.value = baseCurrency
    }

    // --------------------- Accounts ---------------------------------------------------------------
    private suspend fun editAccount(account: Account, newBalance: Double) {
        TestIdlingResource.increment()

        accountCreator.editAccount(account, newBalance) {
            _accounts.value = accountsWithBalance()
        }

        TestIdlingResource.decrement()
    }

    private suspend fun createAccount(data: CreateAccountData) {
        TestIdlingResource.increment()

        accountCreator.createAccount(data) {
            _accounts.value = accountsWithBalance()
        }

        TestIdlingResource.decrement()
    }

    private suspend fun accountsWithBalance(): ImmutableList<AccountBalance> = ioThread {
        accountsAct(Unit)
            .map {
                AccountBalance(
                    account = it,
                    balance = ioThread { accountLogic.calculateAccountBalance(it) }
                )
            }.toImmutableList()
    }

    private suspend fun onAddAccountsDone() {
        TestIdlingResource.increment()

        router.accountsNext()

        TestIdlingResource.decrement()
    }

    private suspend fun onAddAccountsSkip() {
        TestIdlingResource.increment()

        router.accountsSkip()

        TestIdlingResource.decrement()
    }
    // --------------------- Accounts ---------------------------------------------------------------

    // ---------------------------- Categories ------------------------------------------------------
    private suspend fun editCategory(updatedCategory: Category) {
        TestIdlingResource.increment()

        categoryCreator.editCategory(updatedCategory) {
            _categories.value = categoriesAct(Unit)!!
        }

        TestIdlingResource.decrement()
    }

    private suspend fun createCategory(data: CreateCategoryData) {
        TestIdlingResource.increment()

        categoryCreator.createCategory(data) {
            _categories.value = categoriesAct(Unit)!!

            TestIdlingResource.decrement()
        }
    }

    private suspend fun onAddCategoriesDone() {
        TestIdlingResource.increment()

        router.categoriesNext(baseCurrency = _currency.value)

        TestIdlingResource.decrement()
    }

    private suspend fun onAddCategoriesSkip() {
        TestIdlingResource.increment()

        router.categoriesSkip(baseCurrency = _currency.value)

        TestIdlingResource.decrement()
    }
    // ---------------------------- Categories ------------------------------------------------------
}
