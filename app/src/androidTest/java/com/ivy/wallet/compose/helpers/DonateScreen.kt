package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class DonateScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun verifyAmount(text: String): DonateScreen<A> {
        composeTestRule.onNodeWithTag("donation_amount")
            .assertTextEquals(text)
        return this
    }

    fun clickMinus(): DonateScreen<A> {
        composeTestRule.onNodeWithContentDescription("btn_minus")
            .performClick()
        return this
    }

    fun clickPlus(): DonateScreen<A> {
        composeTestRule.onNodeWithContentDescription("btn_plus")
            .performClick()
        return this
    }

    fun clickDonate(): DonateScreen<A> {
        composeTestRule.onNodeWithTag("btn_donate")
            .assertIsDisplayed()
            .performClick()
        return this
    }
}