package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule

class LoanRecordModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val amountInput = AmountInput(composeTestRule)

    fun enterAmount(amount: String) {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()

        inputAmountOpenModal(amount)
    }

    fun inputAmountOpenModal(amount: String) {
        amountInput.enterNumber(amount)
    }

    fun enterNote(note: String) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(note)
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
}