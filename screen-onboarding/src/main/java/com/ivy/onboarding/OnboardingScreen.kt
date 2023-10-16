package com.ivy.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.legacy.data.model.AccountBalance
import com.ivy.navigation.OnboardingScreen
import com.ivy.onboarding.steps.OnboardingAccounts
import com.ivy.onboarding.steps.OnboardingCategories
import com.ivy.onboarding.steps.OnboardingSetCurrency
import com.ivy.onboarding.steps.OnboardingSplashLogin
import com.ivy.onboarding.steps.OnboardingType
import com.ivy.onboarding.viewmodel.OnboardingViewModel
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.legacy.datamodel.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.legacy.utils.OpResult
import com.ivy.legacy.utils.onScreenStart
import kotlinx.collections.immutable.ImmutableList

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.OnboardingScreen(screen: OnboardingScreen) {
    val viewModel: OnboardingViewModel = viewModel()

    val state by viewModel.state
    val uiState = viewModel.uiState()

//    val currency by viewModel.currency.observeAsState(IvyCurrency.getDefault())
//    val opGoogleSign by viewModel.opGoogleSignIn.observeAsState()
//
//    val accountSuggestions by viewModel.accountSuggestions.observeAsState(emptyList())
//    val accounts by viewModel.accounts.observeAsState(listOf())
//
//    val categorySuggestions by viewModel.categorySuggestions.observeAsState(emptyList())
//    val categories by viewModel.categories.observeAsState(emptyList())

    val isSystemDarkTheme = isSystemInDarkTheme()
    onScreenStart {
        viewModel.start(
            screen = screen,
            isSystemDarkMode = isSystemDarkTheme
        )
    }

    UI(
        onboardingState = state,
        currency = uiState.currency,
        opGoogleSignIn = uiState.opGoogleSignIn,

        accountSuggestions = uiState.accountSuggestions,
        accounts = uiState.accounts,

        categorySuggestions = uiState.categorySuggestions,
        categories = uiState.categories,

        onEvent = viewModel::onEvent

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
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    onboardingState: OnboardingState,
    currency: IvyCurrency,
    opGoogleSignIn: OpResult<Unit>?,

    accountSuggestions: ImmutableList<CreateAccountData>,
    accounts: ImmutableList<AccountBalance>,

    categorySuggestions: ImmutableList<CreateCategoryData>,
    categories: ImmutableList<Category>,

//    onLoginWithGoogle: () -> Unit = {},
//    onSkip: () -> Unit = {},
//
//    onStartImport: () -> Unit = {},
//    onStartFresh: () -> Unit = {},
//
//    onSetCurrency: (IvyCurrency) -> Unit = {},
//
//    onCreateAccount: (CreateAccountData) -> Unit = { },
//    onEditAccount: (Account, Double) -> Unit = { _, _ -> },
//    onAddAccountsDone: () -> Unit = {},
//    onAddAccountsSkip: () -> Unit = {},
//
//    onCreateCategory: (CreateCategoryData) -> Unit = {},
//    onEditCategory: (Category) -> Unit = {},
//    onAddCategoryDone: () -> Unit = {},
//    onAddCategorySkip: () -> Unit = {},

    onEvent: (OnboardingEvent) -> Unit = {}
) {
    when (onboardingState) {
        OnboardingState.SPLASH, OnboardingState.LOGIN -> {
            OnboardingSplashLogin(
                onboardingState = onboardingState,
                opGoogleSignIn = opGoogleSignIn,

                onLoginWithGoogle = { onEvent(OnboardingEvent.loginWithGoogle) },
                onSkip = { onEvent(OnboardingEvent.loginOfflineAccount) }
            )
        }

        OnboardingState.CHOOSE_PATH -> {
            OnboardingType(
                onStartImport = { onEvent(OnboardingEvent.startImport) },
                onStartFresh = { onEvent(OnboardingEvent.startFresh) }
            )
        }

        OnboardingState.CURRENCY -> {
            OnboardingSetCurrency(
                preselectedCurrency = currency,
                onSetCurrency = { onEvent(OnboardingEvent.setBaseCurrency(it)) }
            )
        }

        OnboardingState.ACCOUNTS -> {
            OnboardingAccounts(
                baseCurrency = currency.code,
                suggestions = accountSuggestions,
                accounts = accounts,

                onCreateAccount = { onEvent(OnboardingEvent.createAccount(it)) },
                onEditAccount = { account, newBalance ->
                    onEvent(
                        OnboardingEvent.editAccount(
                            account,
                            newBalance
                        )
                    )
                },

                onDone = { onEvent(OnboardingEvent.onAddAccountsDone) },
                onSkip = { onEvent(OnboardingEvent.onAddAccountsSkip) }
            )
        }

        OnboardingState.CATEGORIES -> {
            OnboardingCategories(
                suggestions = categorySuggestions,
                categories = categories,

                onCreateCategory = { onEvent(OnboardingEvent.createCategory(it)) },
                onEditCategory = { onEvent(OnboardingEvent.editCategory(it)) },

                onDone = { onEvent(OnboardingEvent.onAddCategoriesDone) },
                onSkip = { onEvent(OnboardingEvent.onAddCategoriesSkip) }
            )
        }
    }
}

// @ExperimentalFoundationApi
// @Preview
// @Composable
// private fun PreviewOnboarding() {
//    IvyWalletPreview {
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
// }
