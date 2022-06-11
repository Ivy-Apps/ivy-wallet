package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.printTree

class HomeMoreMenu(
    private val composeTestRule: IvyComposeTestRule
) {

    fun closeMoreMenu(): HomeTab {
        composeTestRule.onNodeWithTag("home_more_menu_arrow")
            .performClick()
        return HomeTab(composeTestRule)
    }

    fun clickPlannedPayments() {
        composeTestRule.onNodeWithText("Planned\nPayments")
            .performClick()
    }

    fun clickBudgets(): BudgetsScreen {
        composeTestRule.onNodeWithText("Budgets")
            .performClick()
        return BudgetsScreen(composeTestRule)
    }

    fun clickCategories(): CategoriesScreen {
        composeTestRule.onNodeWithText("Categories")
            .performClick()
        return CategoriesScreen(composeTestRule)
    }

    fun clickSavingsGoal(): SavingsGoalModal {
        composeTestRule.onNodeWithText("Savings goal")
            .performClick()
        return SavingsGoalModal(composeTestRule)
    }

    fun assertSavingsGoal(
        amount: String,
        currency: String = "USD"
    ): HomeMoreMenu {
        composeTestRule.printTree()

        composeTestRule.onNode(
            hasTestTag("savings_goal_row")
        ).assertTextContains(amount)
        return this
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