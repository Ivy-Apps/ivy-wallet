package com.ivy.wallet.compose.component.amountinput

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class CalculatorAmountInput(
    composeTestRule: IvyComposeTestRule
) : IvyAmountInput(composeTestRule) {
    fun enterNumber(
        number: String,
    ): CalculatorAmountInput {
        return super.enterNumber(
            number = number,
            next = CalculatorAmountInput(composeTestRule),
            onCalculator = true,
            autoPressNonCalculator = false
        )
    }

    fun pressPlus(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_+")
            .performClick()
        return this
    }

    fun pressMinus(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_-")
            .performClick()
        return this
    }

    fun pressMultiplication(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_*")
            .performClick()
        return this
    }

    fun pressDivision(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_/")
            .performClick()
        return this
    }

    fun pressLeftBracket(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_(")
            .performClick()
        return this
    }

    fun pressRightBracket(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_)")
            .performClick()
        return this
    }

    fun pressCalcEqual(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("key_=")
            .performClick()
        return this
    }

    fun clickCalcSet(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("calc_set")
            .performClick()
        return this
    }
}