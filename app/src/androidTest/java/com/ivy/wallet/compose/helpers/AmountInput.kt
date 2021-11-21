package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class AmountInput<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun enterNumber(number: String) {
        composeTestRule.waitForIdle()

        for (char in number) {
            when (char) {
                in '0'..'9' -> pressNumber(char.toString().toInt())
                ',' -> {
                    //do nothing
                }
                '.' -> pressDecimalSeparator()
            }
        }

        clickSet()
    }

    fun pressNumber(number: Int) {
        composeTestRule.onNode(hasTestTag("key_$number"))
            .performClick()
    }

    fun pressDel() {
        composeTestRule.onNode(hasTestTag("key_del"))
            .performClick()
    }

    fun pressDecimalSeparator() {
        composeTestRule.onNode(hasTestTag("key_decimal_separator"))
            .performClick()
    }

    fun clickSet() {
        composeTestRule.onNode(hasText("Enter"))
            .performClick()
    }
}