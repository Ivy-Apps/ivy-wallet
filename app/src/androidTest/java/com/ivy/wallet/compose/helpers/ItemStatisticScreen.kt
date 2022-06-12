package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
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
            .performScrollTo()
            .performClick()
    }

    fun clickUpcoming() {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).performClick()
    }

    fun clickTransactionSkip(){
        composeTestRule.onNode(
            hasText("Skip")
                .and(hasAnyAncestor(hasTestTag("transaction_card")))
        )
            .performScrollTo()
            .performClick()
    }

    fun clickTransactionPay(){
        composeTestRule.onNode(
            hasText("Pay")
                .and(hasAnyAncestor(hasTestTag("transaction_card")))
        )
            .performScrollTo()
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