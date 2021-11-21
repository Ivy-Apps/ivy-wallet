package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.printTree

class AccountsTab<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun assertAccountBalance(
        account: String,
        balance: String,
        balanceDecimal: String,
        currency: String = "USD"
    ) {
        composeTestRule.printTree()

        composeTestRule.onNode(
            hasText(account)
                .and(hasAnyDescendant(hasTextExactly(balance, balanceDecimal, currency)))
        ).assertExists()
    }
}