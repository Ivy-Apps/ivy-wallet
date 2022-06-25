package com.ivy.wallet.compose.component

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class PieChartScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    fun assertTitle(title: String): PieChartScreen {
        composeTestRule.onNodeWithTag("piechart_title")
            .assertTextContains(title)
        return this
    }

    fun assertTotalAmount(
        amountInt: String,
        decimalPart: String,
        currency: String = "USD"
    ): PieChartScreen {
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

        return this
    }
}