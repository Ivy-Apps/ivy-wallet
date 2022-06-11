package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class SavingsGoalModal(
    private val composeTestRule: IvyComposeTestRule
) {
    private val amountInput = IvyAmountInput(composeTestRule)

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