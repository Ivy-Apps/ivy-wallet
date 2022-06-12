package com.ivy.wallet.compose.component.amountinput

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

open class IvyAmountInput(
    protected val composeTestRule: IvyComposeTestRule
) {
    //TODO: Refactor this method
    open fun <N> enterNumber(
        number: String,
        next: N,

        onCalculator: Boolean = false,
        autoPressNonCalculator: Boolean = true,
    ): N {
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
            clickSet(next)
        }

        return next
    }

    private fun pressNumber(number: Int, onCalculator: Boolean) {
        composeTestRule.onNode(
            hasTestTag(
                if (onCalculator) "calc_key_$number" else "key_$number"
            )
        )
            .performClick()
    }

    fun pressDel(): IvyAmountInput {
        composeTestRule.onNode(hasTestTag("key_del"))
            .performClick()
        return this
    }

    fun pressDecimalSeparator(
        onCalculator: Boolean
    ): IvyAmountInput {
        composeTestRule.onNode(
            hasTestTag(
                if (onCalculator) "calc_key_decimal_separator" else "key_decimal_separator"
            )
        )
            .performClick()
        return this
    }

    fun <N> clickSet(next: N): N {
        composeTestRule.onNode(hasText("Enter"))
            .performClick()
        return next
    }

    fun clickCalculator(): CalculatorAmountInput {
        composeTestRule.onNodeWithTag("btn_calculator")
            .performClick()
        return CalculatorAmountInput(composeTestRule)
    }
}

interface AmountInput<T> {
    fun enterAmount(number: String): T
}