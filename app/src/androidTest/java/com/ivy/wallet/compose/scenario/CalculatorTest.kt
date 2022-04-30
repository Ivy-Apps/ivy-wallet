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
    fun calcAmount_viaExtraction() = testWithRetry {
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

        homeTab.dismissPrompt()

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
    fun setAmount_withAddition() = testWithRetry {
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

        homeTab.dismissPrompt()

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
    fun calcAmount_viaDivision() = testWithRetry {
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

        homeTab.dismissPrompt()

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
    fun setAmount_withMultiplication_percentDiscount() = testWithRetry {
        onboarding.quickOnboarding()
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddIncome()

        amountInput.enterNumber(
            number = "83,000.50",
            autoPressNonCalculator = false
        )
        amountInput.clickCalculator()

        //---------------------------

        amountInput.pressMultiplication()

        amountInput.enterNumber(
            number = "0.9",
            onCalculator = true
        )

        amountInput.clickCalcSet()
        amountInput.clickSet()

        transactionScreen.skipCategory()
        transactionScreen.editTitle("Calc 4")
        transactionScreen.clickAdd()

        //----------------------------------------

        homeTab.dismissPrompt()

        //83,000.50 * 0.9 = 74,700.45
        homeTab.assertBalance(
            amount = "74,700",
            amountDecimal = ".45"
        )

        homeTab.clickTransaction(
            amount = "74,700.45",
            title = "Calc 4"
        )
    }

    @Test
    fun calcAmount_complexExpression() = testWithRetry {
        onboarding.quickOnboarding()
        mainBottomBar.clickAddFAB()
        mainBottomBar.clickAddExpense()

        amountInput.clickCalculator()
        //---------------------------

        //(523.90+16.7-4+2345.88)*0.9*0.7

        amountInput.pressLeftBracket()
        amountInput.enterNumber("523.90", onCalculator = true)
        amountInput.pressPlus()
        amountInput.enterNumber("16.7", onCalculator = true)
        amountInput.pressMinus()
        amountInput.enterNumber("4", onCalculator = true)
        amountInput.pressPlus()
        amountInput.enterNumber("2345.88", onCalculator = true)
        amountInput.pressRightBracket()
        amountInput.pressMultiplication()
        amountInput.enterNumber("0.9", onCalculator = true)
        amountInput.pressMultiplication()
        amountInput.enterNumber("0.7", onCalculator = true)


        //+ 10 =
        amountInput.pressCalcEqual()
        amountInput.pressPlus()
        amountInput.enterNumber("10", onCalculator = true)
        amountInput.pressCalcEqual()

        amountInput.clickCalcSet()
        amountInput.clickSet()
        transactionScreen.skipCategory()
        transactionScreen.editTitle("Calc Complex")
        transactionScreen.clickAdd()

        //---------------------------------------------------------

        homeTab.dismissPrompt()

        //(523.90+16.7-4+2345.88)*0.9*0.7 = 1815.9624 ; 1815.9624 + 10; = 1,825.96
        homeTab.assertBalance(
            amount = "-1,825",
            amountDecimal = ".96"
        )

        homeTab.clickTransaction(
            amount = "1,825.96",
            title = "Calc Complex"
        )
    }
}