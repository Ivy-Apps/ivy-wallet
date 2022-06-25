package com.ivy.wallet.compose.component.budget

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class BudgetsScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    fun clickAddBudget(): BudgetModal {
        composeTestRule.onNodeWithText("Add budget")
            .performClick()
        return BudgetModal(composeTestRule)
    }

    fun assertBudgetsInfo(
        appBudget: String?,
        categoryBudget: String?,
        currency: String = "USD"
    ): BudgetsScreen {
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

        return this
    }

    fun clickBudget(
        budgetName: String
    ): BudgetModal {
        composeTestRule.onNodeWithText(budgetName)
            .performScrollTo()
            .performClick()

        return BudgetModal(composeTestRule)
    }

    fun assertBudgetDoesNotExist(
        budgetName: String
    ): BudgetsScreen {
        composeTestRule.onNodeWithText(budgetName)
            .assertDoesNotExist()

        return this
    }

}