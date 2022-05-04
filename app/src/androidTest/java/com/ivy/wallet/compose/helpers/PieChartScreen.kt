package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class PieChartScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun assertTitle(title: String) {
        composeTestRule.onNodeWithTag("piechart_title")
            .assertTextContains(title)
    }

    fun assertTotalAmount(
        amountInt: String,
        decimalPart: String,
        currency: String = "USD"
    ) {
        val matchText: (String) -> SemanticsMatcher = { text ->
            hasTestTag("piechart_total_amount")
                .and(
                    hasAnyDescendant(
                        hasText(text)
                    )
                )
        }

        composeTestRule.onNode(matchText(amountInt))
            .assertIsDisplayed()

        composeTestRule.onNode(matchText(decimalPart))
            .assertIsDisplayed()

        composeTestRule.onNode(matchText(currency))
            .assertIsDisplayed()
    }
}