package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

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
}