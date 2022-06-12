package com.ivy.wallet.compose.component

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

class DonateScreen(
    private val composeTestRule: IvyComposeTestRule
) {

    fun verifyAmount(text: String): DonateScreen {
        composeTestRule.onNodeWithTag("donation_amount")
            .assertTextEquals(text)
        return this
    }

    fun clickMinus(): DonateScreen {
        composeTestRule.onNodeWithContentDescription("btn_minus")
            .performClick()
        return this
    }

    fun clickPlus(): DonateScreen {
        composeTestRule.onNodeWithContentDescription("btn_plus")
            .performClick()
        return this
    }

    fun clickDonate(): DonateScreen {
        composeTestRule.onNodeWithTag("btn_donate")
            .assertIsDisplayed()
            .performClick()
        return this
    }
}