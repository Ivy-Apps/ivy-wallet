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
            accounts = _accounts.value,
            accountSuggestions = _accountSuggestions.value,
            categories = _categories.value,
            categorySuggestions = _categorySuggestions.value
        )
    }

    private val router = OnboardingRouter(
        state = _state,
        opGoogleSignIn = _opGoogleSignIn,
        accounts = _accounts,
        accountSuggestions = _accountSuggestions,
        categories = _categories,
        categorySuggestions = _categorySuggestions,

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
                settingsWriter.save(
                    Settings(
                        theme = if (isSystemDarkMode) Theme.DARK else Theme.LIGHT,
                        name = "",
                        baseCurrency = defaultCurrency.code,
                        bufferAmount = 1000.0.toBigDecimal()
                    ).toEntity()
                )
            }
        }
    }

    override fun onEvent(event: OnboardingEvent) {
        viewModelScope.launch {
            when (event) {
                is OnboardingEvent.CreateAccount -> createAccount(event.data)
                is OnboardingEvent.CreateCategory -> createCategory(event.data)
                is OnboardingEvent.EditAccount -> editAccount(event.account, event.newBalance)
                is OnboardingEvent.EditCategory -> editCategory(event.updatedCategory)
                is OnboardingEvent.ImportFinished -> importFinished(event.success)
                OnboardingEvent.ImportSkip -> importSkip()
                OnboardingEvent.LoginOfflineAccount -> loginOfflineAccount()
                OnboardingEvent.LoginWithGoogle -> loginWithGoogle()
                OnboardingEvent.OnAddAccountsDone -> onAddAccountsDone()
                OnboardingEvent.OnAddAccountsSkip -> onAddAccountsSkip()
                OnboardingEvent.OnAddCategoriesDone -> onAddCategoriesDone()
                OnboardingEvent.OnAddCategoriesSkip -> onAddCategoriesSkip()
                is OnboardingEvent.SetBaseCurrency -> setBaseCurrency(event.baseCurrency)
                OnboardingEvent.StartFresh -> startFresh()
                OnboardingEvent.StartImport -> startImport()
            }
        }
    }

    // Step 1 ---------------------------------------------------------------------------------------
    private suspend fun loginWithGoogle() {
        ivyContext.googleSignIn { idToken ->
            if (idToken != null) {
                _opGoogleSignIn.value = OpResult.loading()
                viewModelScope.launch {
                    try {
                        router.googleLoginNext()
                        _opGoogleSignIn.value = null // reset login with Google operation state
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

    private suspend fun loginOfflineAccount() {
        router.offlineAccountNext()
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
        updateBaseCurrency(baseCurrency)
        router.setBaseCurrencyNext(
            baseCurrency = baseCurrency,
            accountsWithBalance = { accountsWithBalance() }
        )
    }

    private suspend fun updateBaseCurrency(baseCurrency: IvyCurrency) {
        ioThread {
            settingsWriter.save(
                settingsDao.findFirst().copy(
                    currency = baseCurrency.code
                )
            )
        }
        _currency.value = baseCurrency
    }

    // --------------------- Accounts ---------------------------------------------------------------
    private suspend fun editAccount(account: Account, newBalance: Double) {
        accountCreator.editAccount(account, newBalance) {
            _accounts.value = accountsWithBalance()
        }
    }

    private suspend fun createAccount(data: CreateAccountData) {
        accountCreator.createAccount(data) {
            _accounts.value = accountsWithBalance()
        }
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
        router.accountsNext()
    }

    private suspend fun onAddAccountsSkip() {
        router.accountsSkip()
    }
    // --------------------- Accounts ---------------------------------------------------------------

    // ---------------------------- Categories ------------------------------------------------------
    private suspend fun editCategory(updatedCategory: Category) {
        categoryCreator.editCategory(updatedCategory) {
            _categories.value = categoriesAct(Unit)!!
        }
    }

    private suspend fun createCategory(data: CreateCategoryData) {
        categoryCreator.createCategory(data) {
            _categories.value = categoriesAct(Unit)!!
        }
    }

    private suspend fun onAddCategoriesDone() {
        router.categoriesNext(baseCurrency = _currency.value)
    }

    private suspend fun onAddCategoriesSkip() {
        router.categoriesSkip(baseCurrency = _currency.value)
    }
    // ---------------------------- Categories ------------------------------------------------------
}
