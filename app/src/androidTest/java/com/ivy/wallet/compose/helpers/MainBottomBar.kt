package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.clickWithRetry

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
        composeTestRule.onNode(hasText("ADD INCOME"))
            .performClick()
    }

    fun clickAddExpense() {
        composeTestRule.onNode(hasText("ADD EXPENSE"))
            .performClick()
    }

    fun clickAddTransfer() {
        composeTestRule.onNode(hasText("ACCOUNT TRANSFER"))
            .performClick()
    }

    fun clickAddPlannedPayment() {
        composeTestRule.onNodeWithText("Add planned payment")
            .performClick()
    }
}