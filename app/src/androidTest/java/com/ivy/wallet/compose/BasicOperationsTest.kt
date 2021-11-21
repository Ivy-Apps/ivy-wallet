package com.ivy.wallet.compose

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.helpers.AmountInput
import com.ivy.wallet.compose.helpers.OnboardingFlow
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BasicOperationsTest : IvyComposeTest() {

    @Test
    fun contextLoads() {
    }

    @Test
    fun OnboardAndAdjustBalance() {
        val onboarding = OnboardingFlow(composeTestRule)
        val amountInput = AmountInput(composeTestRule)

        onboarding.quickOnboarding()

        composeTestRule.onNode(hasText("To accounts"))
            .performClick()

        composeTestRule.onNode(hasText("Cash"))
            .performClick()

        composeTestRule
            .onNode(hasText("Edit"))
            .performClick()

        composeTestRule
            .onNode(hasTestTag("amount_balance"))
            .performClick()

        amountInput.pressNumber(1)

        composeTestRule.printTree()
//        amountInput.pressNumber(0)
//        amountInput.pressNumber(2)
//        amountInput.pressNumber(5)
//        amountInput.pressDecimalSeparator()
//        amountInput.pressNumber(9)
//        amountInput.pressNumber(8)
    }
}