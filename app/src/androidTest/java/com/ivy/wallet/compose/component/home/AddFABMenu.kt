package com.ivy.wallet.compose.component.home

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.clickWithRetry
import com.ivy.wallet.compose.component.edittrn.screen.IncomeExpenseScreen
import com.ivy.wallet.compose.component.edittrn.screen.TransferScreen
import com.ivy.wallet.compose.component.planned.EditPlannedScreen
import com.ivy.wallet.compose.performClickWithRetry

class AddFABMenu(
    private val composeTestRule: IvyComposeTestRule
) {
    fun clickAddIncome(): IncomeExpenseScreen {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("ADD INCOME")),
            maxRetries = 3
        )
        return IncomeExpenseScreen(composeTestRule)
    }

    fun clickAddExpense(): IncomeExpenseScreen {
        composeTestRule.onNode(hasText("ADD EXPENSE"))
            .performClickWithRetry(composeTestRule)
        return IncomeExpenseScreen(composeTestRule)
    }

    fun clickAddTransfer(): TransferScreen {
        composeTestRule.onNode(hasText("ACCOUNT TRANSFER"))
            .performClickWithRetry(composeTestRule)
        return TransferScreen(composeTestRule)
    }

    fun clickAddPlannedPayment(): EditPlannedScreen {
        composeTestRule.onNodeWithText("Add planned payment")
            .performClickWithRetry(composeTestRule)
        return EditPlannedScreen(composeTestRule)
    }
}