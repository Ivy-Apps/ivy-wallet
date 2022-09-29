//package com.ivy.onboarding
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.BoxWithConstraintsScope
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.ivy.base.AccountBalance
//import com.ivy.data.AccountOld
//import com.ivy.data.CategoryOld
//import com.ivy.data.IvyCurrency
//import com.ivy.design.util.IvyPreview
//
//import com.ivy.onboarding.steps.*
//import com.ivy.onboarding.viewmodel.OnboardingViewModel
//import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
//import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
//import com.ivy.wallet.utils.OpResult
//
//@ExperimentalFoundationApi
//@Composable
//fun BoxWithConstraintsScope.OnboardingScreen() {
//    val viewModel: OnboardingViewModel = hiltViewModel()
//
//    val state by viewModel.state.observeAsState(OnboardingState.SPLASH)
//    val currency by viewModel.currency.observeAsState(IvyCurrency.getDefault())
//    val opGoogleSign by viewModel.opGoogleSignIn.observeAsState()
//
//    val accountSuggestions by viewModel.accountSuggestions.observeAsState(emptyList())
//    val accounts by viewModel.accounts.observeAsState(listOf())
//
//    val categorySuggestions by viewModel.categorySuggestions.observeAsState(emptyList())
//    val categories by viewModel.categories.observeAsState(emptyList())
//
//    val isSystemDarkTheme = isSystemInDarkTheme()
//
//    UI(
//        onboardingState = state,
//        currency = currency,
//        opGoogleSignIn = opGoogleSign,
//
//        accountSuggestions = accountSuggestions,
//        accounts = accounts,
//
//        categorySuggestions = categorySuggestions,
//        categories = categories,
//
//        onLoginWithGoogle = viewModel::loginWithGoogle,
//        onSkip = viewModel::loginOfflineAccount,
//
//        onStartImport = viewModel::startImport,
//        onStartFresh = viewModel::startFresh,
//
//        onSetCurrency = viewModel::setBaseCurrency,
//
//        onCreateAccount = viewModel::createAccount,
//        onEditAccount = viewModel::editAccount,
//        onAddAccountsDone = viewModel::onAddAccountsDone,
//        onAddAccountsSkip = viewModel::onAddAccountsSkip,
//
//        onCreateCategory = viewModel::createCategory,
//        onEditCategory = viewModel::editCategory,
//        onAddCategoryDone = viewModel::onAddCategoriesDone,
//        onAddCategorySkip = viewModel::onAddCategoriesSkip
//    )
//}
//
//@ExperimentalFoundationApi
//@Composable
//private fun BoxWithConstraintsScope.UI(
//    onboardingState: OnboardingState,
//    currency: IvyCurrency,
//    opGoogleSignIn: OpResult<Unit>?,
//
//    accountSuggestions: List<CreateAccountData>,
//    accounts: List<AccountBalance>,
//
//    categorySuggestions: List<CreateCategoryData>,
//    categories: List<CategoryOld>,
//
//    onLoginWithGoogle: () -> Unit = {},
//    onSkip: () -> Unit = {},
//
//    onStartImport: () -> Unit = {},
//    onStartFresh: () -> Unit = {},
//
//    onSetCurrency: (IvyCurrency) -> Unit = {},
//
//    onCreateAccount: (CreateAccountData) -> Unit = { },
//    onEditAccount: (AccountOld, Double) -> Unit = { _, _ -> },
//    onAddAccountsDone: () -> Unit = {},
//    onAddAccountsSkip: () -> Unit = {},
//
//    onCreateCategory: (CreateCategoryData) -> Unit = {},
//    onEditCategory: (CategoryOld) -> Unit = {},
//    onAddCategoryDone: () -> Unit = {},
//    onAddCategorySkip: () -> Unit = {},
//) {
//    when (onboardingState) {
//        OnboardingState.SPLASH, OnboardingState.LOGIN -> {
//            OnboardingSplashLogin(
//                onboardingState = onboardingState,
//                opGoogleSignIn = opGoogleSignIn,
//
//                onLoginWithGoogle = onLoginWithGoogle,
//                onSkip = onSkip
//            )
//        }
//        OnboardingState.CHOOSE_PATH -> {
//            OnboardingType(
//                onStartImport = onStartImport,
//                onStartFresh = onStartFresh
//            )
//        }
//        OnboardingState.CURRENCY -> {
//            OnboardingSetCurrency(
//                preselectedCurrency = currency,
//                onSetCurrency = onSetCurrency
//            )
//        }
//        OnboardingState.ACCOUNTS -> {
//            OnboardingAccounts(
//                baseCurrency = currency.code,
//                suggestions = accountSuggestions,
//                accounts = accounts,
//
//                onCreateAccount = onCreateAccount,
//                onEditAccount = onEditAccount,
//
//                onDone = onAddAccountsDone,
//                onSkip = onAddAccountsSkip
//            )
//        }
//        OnboardingState.CATEGORIES -> {
//            OnboardingCategories(
//                suggestions = categorySuggestions,
//                categories = categories,
//
//                onCreateCategory = onCreateCategory,
//                onEditCategory = onEditCategory,
//
//                onDone = onAddCategoryDone,
//                onSkip = onAddCategorySkip
//            )
//        }
//    }
//}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun PreviewOnboarding() {
//    IvyPreview {
//        UI(
//            accountSuggestions = listOf(),
//            accounts = listOf(),
//
//            categorySuggestions = listOf(),
//            categories = listOf(),
//
//            onboardingState = OnboardingState.SPLASH,
//            currency = IvyCurrency.getDefault(),
//            opGoogleSignIn = null,
//
//            onLoginWithGoogle = {},
//            onSkip = {},
//            onSetCurrency = {},
//        )
//    }
//}