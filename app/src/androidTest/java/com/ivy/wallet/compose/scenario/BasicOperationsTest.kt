package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.compose.printTree
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BasicOperationsTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val accountModal = AccountModal(composeTestRule)
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val chooseCategoryModal = ChooseCategoryModal(composeTestRule)


    @Test
    fun contextLoads() {
    }

    @Test
    fun OnboardAndAdjustBalance() {
        onboarding.quickOnboarding()

        composeTestRule.onNode(hasText("To accounts"))
            .performClick()

        composeTestRule.onNode(hasText("Cash"))
            .performClick()

        composeTestRule
            .onNode(hasText("Edit"))
            .performClick()

        accountModal.clickBalance()

        amountInput.enterNumber("1,025.98")

        accountModal.clickSave()

        composeTestRule.onNodeWithTag("balance")
            .assertTextEquals("USD", "1,025", ".98")

        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()

        mainBottomBar.clickHome()

        composeTestRule.onNodeWithTag("home_balance")
            .assertTextEquals("USD", "1,025", ".98")
    }

    @Test
    fun CreateIncome() {
        onboarding.quickOnboarding()

        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddIncome()

        amountInput.enterNumber("5,000")

        chooseCategoryModal.selectCategory("Investments")

        composeTestRule.onNodeWithTag("input_field")
            .performTextInput("Salary")

        composeTestRule.onNodeWithText("Add")
            .performClick()

        composeTestRule.waitForIdle()
        composeTestRule.printTree()

        composeTestRule.onNodeWithTag("transaction_card")
            .assertIsDisplayed()
    }
}