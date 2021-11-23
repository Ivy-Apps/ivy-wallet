package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class MainBottomBar<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun clickHome() {
        composeTestRule.onNode(hasTestTag("home"))
            .performClick()
    }

    fun clickAccounts() {
        composeTestRule.onNode(hasTestTag("accounts"))
            .performClick()
    }

    fun clickAddFAB() {
        composeTestRule.onNode(hasTestTag("fab_add"))
            .performClick()
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