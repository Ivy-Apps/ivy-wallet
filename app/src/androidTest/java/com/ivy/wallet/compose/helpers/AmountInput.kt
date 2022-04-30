package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class AmountInput<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    fun enterNumber(
        number: String,
        onCalculator: Boolean = false,
        autoPressNonCalculator: Boolean = true,
    ) {
        composeTestRule.waitForIdle()

        for (char in number) {
            when (char) {
                in '0'..'9' -> pressNumber(
                    number = char.toString().toInt(),
                    onCalculator = onCalculator
                )
                ',' -> {
                    //do nothing
                }
                '.' -> pressDecimalSeparator(
                    onCalculator = onCalculator
                )
            }
        }

        if (!onCalculator && autoPressNonCalculator) {
            clickSet()
        }
    }

    private fun pressNumber(number: Int, onCalculator: Boolean) {
        composeTestRule.onNode(
            hasTestTag(
                if (onCalculator) "calc_key_$number" else "key_$number"
            )
        )
            .performClick()
    }

    fun pressDel() {
        composeTestRule.onNode(hasTestTag("key_del"))
            .performClick()
    }

    fun pressDecimalSeparator(
        onCalculator: Boolean
    ) {
        composeTestRule.onNode(
            hasTestTag(
                if (onCalculator) "calc_key_decimal_separator" else "key_decimal_separator"
            )
        )
            .performClick()
    }

    fun pressPlus() {
        composeTestRule.onNodeWithTag("key_+")
            .performClick()
    }

    fun pressMinus() {
        composeTestRule.onNodeWithTag("key_-")
            .performClick()
    }

    fun pressMultiplication() {
        composeTestRule.onNodeWithTag("key_*")
            .performClick()
    }

    fun pressDivision() {
        composeTestRule.onNodeWithTag("key_/")
            .performClick()
    }

    fun pressLeftBracket() {
        composeTestRule.onNodeWithTag("key_(")
            .performClick()
    }

    fun pressRightBracket() {
        composeTestRule.onNodeWithTag("key_)")
            .performClick()
    }

    fun pressCalcEqual() {
        composeTestRule.onNodeWithTag("key_=")
            .performClick()
    }

    fun clickCalcSet() {
        composeTestRule.onNodeWithTag("calc_set")
            .performClick()
    }

    fun clickSet() {
        composeTestRule.onNode(hasText("Enter"))
            .performClick()
    }

    fun clickCalculator() {
        composeTestRule.onNodeWithTag("btn_calculator")
            .performClick()
    }
}