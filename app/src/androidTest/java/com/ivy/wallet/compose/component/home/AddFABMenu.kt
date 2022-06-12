package com.ivy.wallet.compose.component.home

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.clickWithRetry
import com.ivy.wallet.compose.component.edittrn.EditPlannedScreen
import com.ivy.wallet.compose.component.edittrn.TransactionScreen
import com.ivy.wallet.compose.performClickWithRetry

class AddFABMenu(
    private val composeTestRule: IvyComposeTestRule
) {
    fun clickAddIncome(): TransactionScreen {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("ADD INCOME")),
            maxRetries = 3
        )
        return TransactionScreen(composeTestRule)
    }

    fun clickAddExpense(): TransactionScreen {
        composeTestRule.onNode(hasText("ADD EXPENSE"))
            .performClickWithRetry(composeTestRule)
        return TransactionScreen(composeTestRule)
    }

    fun clickAddTransfer(): TransactionScreen {
        composeTestRule.onNode(hasText("ACCOUNT TRANSFER"))
            .performClickWithRetry(composeTestRule)
        return TransactionScreen(composeTestRule)
    }

    fun clickAddPlannedPayment(): EditPlannedScreen {
        composeTestRule.onNodeWithText("Add planned payment")
            .performClickWithRetry(composeTestRule)
        return EditPlannedScreen(composeTestRule)
    }
}