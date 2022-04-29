package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class CalculatorTest : IvyComposeTest() {
    private val onboarding = OnboardingFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val transactionScreen = TransactionScreen(composeTestRule)


    @Test
    fun calcAmount_viaExtraction() {
        onboarding.quickOnboarding()
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddExpense()

        //---------------------------
        amountInput.clickCalculator()

        amountInput.enterNumber(
            number = "21",
            onCalculator = true
        )

        amountInput.pressMinus()

        amountInput.enterNumber(
            number = "3.52",
            onCalculator = true
        )

        amountInput.clickCalcSet()
        amountInput.clickSet()

        transactionScreen.skipCategory()

        transactionScreen.editTitle("Calc 1")

        transactionScreen.clickAdd()

        //----------------------------------------

        //21 - 3.52 = 17.48
        homeTab.assertBalance(
            amount = "-17",
            amountDecimal = ".48"
        )

        homeTab.clickTransaction(
            amount = "17.48",
            title = "Calc 1"
        )
    }

    @Test
    fun setAmount_withAddition() {

    }

    @Test
    fun calcAmount_viaDivision() {

    }

    @Test
    fun setAmount_withMultiplication() {

    }

    @Test
    fun calcComplexExpression() {

    }
}