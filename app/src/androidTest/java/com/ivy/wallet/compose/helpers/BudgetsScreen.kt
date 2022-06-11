package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class BudgetsScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    fun clickAddBudget() {
        composeTestRule.onNodeWithText("Add budget")
            .performClick()
    }

    fun assertBudgetsInfo(
        appBudget: String?,
        categoryBudget: String?,
        currency: String = "USD"
    ) {
        val budgetInfoNode = composeTestRule.onNodeWithTag("budgets_info_text")

        when {
            appBudget != null && categoryBudget == null -> {
                budgetInfoNode.assertTextEquals("Budget info: $appBudget $currency app budget")
            }
            appBudget == null && categoryBudget != null -> {
                budgetInfoNode.assertTextEquals("Budget info: $categoryBudget $currency for categories")
            }
            appBudget != null && categoryBudget != null -> {
                budgetInfoNode.assertTextEquals("Budget info: $categoryBudget $currency for categories / $appBudget $currency app budget")
            }
            appBudget == null && categoryBudget == null -> {
                budgetInfoNode.assertDoesNotExist()
            }
            else -> error("Unexpected case")
        }
    }

    fun clickBudget(
        budgetName: String
    ) {
        composeTestRule.onNodeWithText(budgetName)
            .performScrollTo()
            .performClick()
    }

    fun assertBudgetDoesNotExist(
        budgetName: String
    ) {
        composeTestRule.onNodeWithText(budgetName)
            .assertDoesNotExist()
    }

}