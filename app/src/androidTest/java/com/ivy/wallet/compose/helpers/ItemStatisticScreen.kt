package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class ItemStatisticScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun clickDelete() {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
    }

    fun clickEdit() {
        composeTestRule.onNodeWithText("Edit")
            .performClick()
    }

    fun clickClose() {
        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()
    }

    fun assertBalance(
        balance: String,
        balanceDecimal: String,
        currency: String
    ) {
        composeTestRule.onNodeWithTag("balance")
            .assertTextEquals(currency, balance, balanceDecimal)
    }
}