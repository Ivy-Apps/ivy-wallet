package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.printTree

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

    fun clickTransaction(
        amount: String,
        title: String? = null,
        account: String? = null,
        category: String? = null
    ) {
        //TODO: Implement account, category, title selectors
        composeTestRule.printTree()

        var matcher = hasTestTag("transaction_card")
            .and(hasText(amount))

        if (account != null) {
            matcher = matcher.and(hasAnyDescendant(hasText(account)))
        }

        if (category != null) {
            matcher = matcher.and(hasAnyDescendant(hasText(category)))
        }

        if (title != null) {
            matcher = matcher.and(hasText(title))
        }

        composeTestRule.onNode(matcher)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performScrollTo()
            .performClick()
    }
}