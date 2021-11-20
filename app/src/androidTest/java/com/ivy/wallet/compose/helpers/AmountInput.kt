package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.base.localDecimalSeparator

class AmountInput<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun pressNumber(number: Int) {
        composeTestRule.onNode(hasText(number.toString()))
            .performClick()
    }

    fun pressDel() {
        composeTestRule.onNode(hasTestTag("del"))
            .performClick()
    }

    fun pressDecimalSeparator() {
        composeTestRule.onNode(hasText(localDecimalSeparator()))
            .performClick()
    }
}