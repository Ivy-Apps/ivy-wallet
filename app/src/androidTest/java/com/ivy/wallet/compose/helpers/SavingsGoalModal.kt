package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class SavingsGoalModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val amountInput = AmountInput(composeTestRule)

    fun enterAmount(
        amount: String
    ) {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()

        amountInput.enterNumber(amount)
    }

    fun clickSave() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }
}