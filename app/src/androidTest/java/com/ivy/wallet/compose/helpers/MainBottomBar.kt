package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.clickWithRetry
import com.ivy.wallet.compose.performClickWithRetry

class MainBottomBar<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun clickHome() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("home")),
            maxRetries = 3
        )
    }

    fun clickAccounts() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("accounts")),
            maxRetries = 3
        )
    }

    fun clickAddFAB() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("fab_add")),
            maxRetries = 3
        )
    }

    fun clickAddIncome() {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasText("ADD INCOME")),
            maxRetries = 3
        )
    }

    fun clickAddExpense() {
        composeTestRule.onNode(hasText("ADD EXPENSE"))
            .performClickWithRetry(composeTestRule)
    }

    fun clickAddTransfer() {
        composeTestRule.onNode(hasText("ACCOUNT TRANSFER"))
            .performClickWithRetry(composeTestRule)
    }

    fun clickAddPlannedPayment() {
        composeTestRule.onNodeWithText("Add planned payment")
            .performClickWithRetry(composeTestRule)
    }
}