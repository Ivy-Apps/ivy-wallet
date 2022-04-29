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
        onboarding.quickOnboarding()
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddIncome()

        amountInput.enterNumber(
            number = "38.16",
            autoPressNonCalculator = false
        )
        amountInput.clickCalculator()

        //---------------------------

        amountInput.pressPlus()
        amountInput.enterNumber(
            number = "80.74",
            onCalculator = true
        )

        amountInput.clickCalcSet()
        amountInput.clickSet()

        transactionScreen.skipCategory()
        transactionScreen.editTitle("Calc 2")
        transactionScreen.clickAdd()

        //----------------------------

        //38.16 + 80.74 = 118.90
        homeTab.assertBalance(
            amount = "118",
            amountDecimal = ".90"
        )

        homeTab.clickTransaction(
            amount = "118.90",
            title = "Calc 2"
        )
    }

    @Test
    fun calcAmount_viaDivision() {
        onboarding.quickOnboarding()
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddExpense()

        amountInput.clickCalculator()

        //---------------------------

        amountInput.enterNumber(
            number = "72.50",
            onCalculator = true
        )

        amountInput.pressDivision()

        amountInput.enterNumber(
            number = "3",
            onCalculator = true
        )

        amountInput.pressCalcEqual()

        amountInput.clickCalcSet()
        amountInput.clickSet()

        transactionScreen.skipCategory()
        transactionScreen.editTitle("Calc 3")

        transactionScreen.clickAdd()
        //----------------------------------------

        //72.50 / 3 = 24.17
        homeTab.assertBalance(
            amount = "-24",
            amountDecimal = ".17"
        )

        homeTab.clickTransaction(
            amount = "24.17",
            title = "Calc 3"
        )
    }

    @Test
    fun setAmount_withMultiplication() {

    }

    @Test
    fun calcAmount_complexExpression() {

    }
}