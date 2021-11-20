package com.ivy.wallet.compose

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.helpers.AmountInput
import com.ivy.wallet.compose.helpers.OnboardingFlow
import org.junit.Test

class BasicOperationsTest : IvyComposeTest() {

    @Test
    fun OnboardAndAdjustBalance() {
        val onboarding = OnboardingFlow(composeTestRule)
        val amountInput = AmountInput(composeTestRule)

        onboarding.quickOnboarding()

        composeTestRule.onNode(hasText("To accounts"))
            .performClick()

        composeTestRule.onNode(hasText("Cash"))
            .performClick()

        composeTestRule.onNode(hasTestTag("balance"))
            .performClick()

        //TODO: This test breaks

        amountInput.pressNumber(1)
        amountInput.pressNumber(0)
        amountInput.pressNumber(2)
        amountInput.pressNumber(5)
        amountInput.pressDecimalSeparator()
        amountInput.pressNumber(9)
        amountInput.pressNumber(8)
    }
}