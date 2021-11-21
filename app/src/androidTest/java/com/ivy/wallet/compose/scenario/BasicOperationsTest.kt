package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.AccountModal
import com.ivy.wallet.compose.helpers.AmountInput
import com.ivy.wallet.compose.helpers.MainBottomBar
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
        val accountModal = AccountModal(composeTestRule)
        val mainBottomBar = MainBottomBar(composeTestRule)

        onboarding.quickOnboarding()

        composeTestRule.onNode(hasText("To accounts"))
            .performClick()

        composeTestRule.onNode(hasText("Cash"))
            .performClick()

        composeTestRule
            .onNode(hasText("Edit"))
            .performClick()

        accountModal.clickBalance()

        amountInput.pressNumber(1)
        amountInput.pressNumber(0)
        amountInput.pressNumber(2)
        amountInput.pressNumber(5)
        amountInput.pressDecimalSeparator()
        amountInput.pressNumber(9)
        amountInput.pressNumber(8)
        amountInput.clickSet()

        accountModal.clickSave()

        composeTestRule.onNodeWithTag("balance")
            .assertTextEquals("USD", "1,025", ".98")

        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()

        mainBottomBar.clickHome()

        composeTestRule.onNodeWithTag("home_balance")
            .assertTextEquals("USD", "1,025", ".98")
    }
}