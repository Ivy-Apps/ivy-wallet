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
        var matcher = hasTestTag("type_amount_currency")
            .and(hasAnyDescendant(hasText(amount)))

        if (account != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasAnyDescendant(
                        hasText(account)
                    )
                )
            )
        }

        if (category != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasAnyDescendant(
                        hasText(category)
                    )
                )
            )
        }

        if (title != null) {
            matcher = matcher.and(
                hasAnySibling(
                    hasText(title)
                )
            )
        }

        composeTestRule.printTree(
            useUnmergedTree = true
        )

        composeTestRule.onNode(
            matcher = matcher,
            useUnmergedTree = true
        )
            .assertIsDisplayed()
            .performClick()
    }

    fun assertTransactionNotExists(
        amount: String
    ) {
        composeTestRule.onNode(
            hasTestTag("transaction_card")
                .and(hasText(amount))
        ).assertDoesNotExist()
    }

    fun assertUpcomingIncome(
        amount: String,
        currency: String
    ) {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_income",
            useUnmergedTree = true
        ).assertTextEquals("$amount $currency")
    }

    fun assertUpcomingExpense(
        amount: String,
        currency: String
    ) {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_expense",
            useUnmergedTree = true
        ).assertTextEquals("$amount $currency")
    }

    fun dismissPrompt() {
        composeTestRule.onNodeWithContentDescription("prompt_dismiss")
            .performClick()
    }

    fun assertUpcomingDoesNotExist() {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).assertDoesNotExist()
    }

    fun clickUpcoming() {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).performClick()
    }

    fun clickTransactionPay() {
        composeTestRule.onNode(
            hasText("Pay")
                .and(hasAnyAncestor(hasTestTag("transaction_card")))
        )
            .performScrollTo()
            .performClick()
    }

    fun assertGreeting(
        greeting: String
    ) {
        composeTestRule.onNodeWithTag("home_greeting_text", useUnmergedTree = true)
            .assertTextEquals(greeting)
    }

    fun clickIncomeCard() {
        composeTestRule.onNodeWithTag("home_card_income")
            .performClick()
    }

    fun clickExpenseCard() {
        composeTestRule.onNodeWithTag("home_card_expense")
            .performClick()
    }
}