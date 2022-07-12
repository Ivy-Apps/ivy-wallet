package com.ivy.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.accounts.AccountsTab
import com.ivy.base.IvyWalletPreview
import com.ivy.base.MainTab
import com.ivy.base.ivyWalletCtx
import com.ivy.data.transaction.TransactionType
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.home.HomeTab
import com.ivy.screens.EditPlanned
import com.ivy.screens.EditTransaction
import com.ivy.screens.Main
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.MainScreen(screen: Main) {
    val viewModel: MainViewModel = viewModel()

    val currency by viewModel.currency.observeAsState("")

    onScreenStart {
        viewModel.start(screen)
    }

    val ivyContext = ivyWalletCtx()
    UI(
        screen = screen,
        tab = ivyContext.mainTab,
        baseCurrency = currency,
        selectTab = viewModel::selectTab,
        onCreateAccount = viewModel::createAccount
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: Main,
    tab: MainTab,

    baseCurrency: String,

    selectTab: (MainTab) -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit,
) {
    when (tab) {
        MainTab.HOME -> HomeTab(screen = screen)
        MainTab.ACCOUNTS -> AccountsTab(screen = screen)
    }

    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }

    val nav = navigation()
    BottomBar(
        tab = tab,
        selectTab = selectTab,

        onAddIncome = {
            nav.navigateTo(
                EditTransaction(
                    initialTransactionId = null,
                    type = TransactionType.INCOME
                )
            )
        },
        onAddExpense = {
            nav.navigateTo(
                EditTransaction(
                    initialTransactionId = null,
                    type = TransactionType.EXPENSE
                )
            )
        },
        onAddTransfer = {
            nav.navigateTo(
                EditTransaction(
                    initialTransactionId = null,
                    type = TransactionType.TRANSFER
                )
            )
        },
        onAddPlannedPayment = {
            nav.navigateTo(
                EditPlanned(
                    type = TransactionType.EXPENSE,
                    plannedPaymentRuleId = null
                )
            )
        },

        showAddAccountModal = {
            accountModalData = AccountModalData(
                account = null,
                balance = 0.0,
                baseCurrency = baseCurrency
            )
        }
    )

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewMainScreen() {
    IvyWalletPreview {
        UI(
            screen = Main,
            tab = MainTab.HOME,
            baseCurrency = "BGN",
            selectTab = {},
            onCreateAccount = { }
        )
    }
}