package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.printTree

class HomeMoreMenu(
    private val composeTestRule: IvyComposeTestRule
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

    fun clickSettings(): SettingsScreen {
        composeTestRule.onNodeWithText("Settings")
            .performClick()
        return SettingsScreen(composeTestRule)
    }

    fun clickLoans(): LoansScreen {
        composeTestRule.onNodeWithText("Loans")
            .performClick()
        return LoansScreen(composeTestRule)
    }

    fun clickDonate(): DonateScreen {
        composeTestRule.onNodeWithText("Donate")
            .performClick()
        return DonateScreen(composeTestRule)
    }
}