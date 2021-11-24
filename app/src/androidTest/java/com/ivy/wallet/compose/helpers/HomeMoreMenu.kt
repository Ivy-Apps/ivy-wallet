package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.printTree

class HomeMoreMenu<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun clickOpenCloseArrow() {
        composeTestRule.onNodeWithTag("home_more_menu_arrow")
            .performClick()
    }

    fun clickPlannedPayments() {
        composeTestRule.onNodeWithText("Planned\nPayments")
            .performClick()
    }

    fun clickBudgets() {
        composeTestRule.onNodeWithText("Budgets")
            .performClick()
    }

    fun clickCategories() {
        composeTestRule.onNodeWithText("Categories")
            .performClick()
    }

    fun clickSavingsGoal() {
        composeTestRule.onNodeWithText("Savings goal")
            .performClick()
    }

    fun assertSavingsGoal(
        amount: String,
        currency: String = "USD"
    ) {
        composeTestRule.printTree()

        composeTestRule.onNode(
            hasTestTag("savings_goal_row")
        ).assertTextContains(amount)
    }

    fun clickSettings() {
        composeTestRule.onNodeWithText("Settings")
            .performClick()
    }
}