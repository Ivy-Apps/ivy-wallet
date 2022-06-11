package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class BudgetModal(
    private val composeTestRule: IvyComposeTestRule
) {
    private val amountInput = IvyAmountInput(composeTestRule)

    fun enterName(budgetName: String) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(budgetName)
    }

    fun enterAmount(amount: String) {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()

        amountInput.enterNumber(amount)
    }

    fun clickCategory(category: String) {
        composeTestRule.onNode(
            hasText(category)
                .and(hasAnyAncestor(hasTestTag("budget_categories_row"))),
            useUnmergedTree = true
        ).performClick()
    }

    fun clickAdd() {
        composeTestRule.onNodeWithText("Add")
            .performClick()
    }

    fun clickSave() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("modal_delete")
            .performClick()
    }

    fun clickClose() {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()
    }
}