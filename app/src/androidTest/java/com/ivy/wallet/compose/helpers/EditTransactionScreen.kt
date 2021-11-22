package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class EditTransactionScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val amountInput = AmountInput(composeTestRule)
    private val chooseCategoryModal = ChooseCategoryModal(composeTestRule)

    fun editCategory(
        currentCategory: String,
        newCategory: String
    ) {
        composeTestRule.onNodeWithText(currentCategory)
            .performClick()

        chooseCategoryModal.selectCategory(newCategory)
    }

    fun editAmount(
        newAmount: String
    ) {
        composeTestRule.onNodeWithTag("edit_amount_balance_row")
            .performClick()

        amountInput.enterNumber(newAmount)
    }

    fun editAccount(
        newAccount: String
    ) {
        composeTestRule.onNode(
            hasTestTag("from_account")
                .and(hasText(newAccount))
        ).performClick()
    }

    fun editTitle(
        newTitle: String
    ) {
        composeTestRule.onNodeWithTag("input_field")
            .performTextInput(newTitle)
    }

    fun clickClose() {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
    }

    fun save() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }
}