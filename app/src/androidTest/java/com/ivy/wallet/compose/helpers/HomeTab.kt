package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.rules.ActivityScenarioRule

class HomeTab<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun assertBalance(
        amount: String,
        amountDecimal: String,
        currency: String = "USD"
    ) {
        composeTestRule.onNodeWithTag("home_balance")
            .assertTextEquals(currency, amount, amountDecimal)
    }
}