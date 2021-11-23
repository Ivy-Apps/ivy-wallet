package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class BudgetsScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
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

}