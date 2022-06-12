package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput
import com.ivy.wallet.compose.component.edittrn.screen.IncomeExpenseScreen
import com.ivy.wallet.compose.component.home.HomeTab
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class CalculatorTest : IvyComposeTest() {
    //TODO: Refactor CalculatorTest

    @Test
    fun calcAmount_viaExtraction() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddExpense()
            .firstOpen()

            //---------------------------
            .clickCalculator()
            .enterNumber(
                number = "21",
            )
            .pressMinus()
            .enterNumber(
                number = "3.52"
            )
            .clickCalcSet()
            .clickSet(next = IncomeExpenseScreen(composeTestRule))
            .skipCategory()
            .editTitle("Calc 1")
            .clickAdd(next = HomeTab(composeTestRule))
            //----------------------------------------
            .dismissPrompt()
            //21 - 3.52 = 17.48
            .assertBalance(
                amount = "-17",
                amountDecimal = ".48"
            )
            .clickTransaction(
                amount = "17.48",
                title = "Calc 1",
                next = IncomeExpenseScreen(composeTestRule)
            )
    }

    @Test
    fun setAmount_withAddition() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddIncome()
            .firstOpen()
            .enterNumber(
                number = "38.16",
                autoPressNonCalculator = false,
                next = IvyAmountInput(composeTestRule)
            )
            .clickCalculator()
            //---------------------------
            .pressPlus()
            .enterNumber(
                number = "80.74",
            )
            .clickCalcSet()
            .clickSet(next = IncomeExpenseScreen(composeTestRule))
            .skipCategory()
            .editTitle("Calc 2")
            .clickAdd(next = HomeTab(composeTestRule))
            //----------------------------
            .dismissPrompt()
            //38.16 + 80.74 = 118.90
            .assertBalance(
                amount = "118",
                amountDecimal = ".90"
            )
            .clickTransaction(
                amount = "118.90",
                title = "Calc 2",
                next = IncomeExpenseScreen(composeTestRule)
            )
    }

    @Test
    fun calcAmount_viaDivision() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddExpense()
            .firstOpen()
            .clickCalculator()
            //---------------------------
            .enterNumber(
                number = "72.50",
            )
            .pressDivision()
            .enterNumber(
                number = "3",
            )
            .pressCalcEqual()
            .clickCalcSet()
            .clickSet(next = IncomeExpenseScreen(composeTestRule))
            .skipCategory()
            .editTitle("Calc 3")
            .clickAdd(next = HomeTab(composeTestRule))
            //----------------------------------------
            .dismissPrompt()
            //72.50 / 3 = 24.17
            .assertBalance(
                amount = "-24",
                amountDecimal = ".17"
            )
            .clickTransaction(
                amount = "24.17",
                title = "Calc 3",
                next = IncomeExpenseScreen(composeTestRule)
            )
    }

    @Test
    fun setAmount_withMultiplication_percentDiscount() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddIncome()
            .firstOpen()
            .enterNumber(
                number = "83,000.50",
                autoPressNonCalculator = false,
                next = IvyAmountInput(composeTestRule)
            )
            .clickCalculator()
            //---------------------------
            .pressMultiplication()
            .enterNumber(
                number = "0.9",
            )
            .clickCalcSet()
            .clickSet(next = IncomeExpenseScreen(composeTestRule))
            .skipCategory()
            .editTitle("Calc 4")
            .clickAdd(next = HomeTab(composeTestRule))
            //----------------------------------------
            .dismissPrompt()
            //83,000.50 * 0.9 = 74,700.45
            .assertBalance(
                amount = "74,700",
                amountDecimal = ".45"
            )
            .clickTransaction(
                amount = "74,700.45",
                title = "Calc 4",
                next = IncomeExpenseScreen(composeTestRule)
            )
    }

    @Test
    fun calcAmount_complexExpression() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddExpense()
            .firstOpen()
            .clickCalculator()
            //---------------------------
            //(523.90+16.7-4+2345.88)*0.9*0.7
            .pressLeftBracket()
            .enterNumber("523.90")
            .pressPlus()
            .enterNumber("16.7")
            .pressMinus()
            .enterNumber("4")
            .pressPlus()
            .enterNumber("2345.88")
            .pressRightBracket()
            .pressMultiplication()
            .enterNumber("0.9")
            .pressMultiplication()
            .enterNumber("0.7")
            //+ 10 =
            .pressCalcEqual()
            .pressPlus()
            .enterNumber("10")
            .pressCalcEqual()
            .clickCalcSet()
            .clickSet(next = IncomeExpenseScreen(composeTestRule))
            .skipCategory()
            .editTitle("Calc Complex")
            .clickAdd(next = HomeTab(composeTestRule))
            //---------------------------------------------------------
            .dismissPrompt()
            //(523.90+16.7-4+2345.88)*0.9*0.7 = 1815.9624 ; 1815.9624 + 10; = 1,825.96
            .assertBalance(
                amount = "-1,825",
                amountDecimal = ".96"
            )
            .clickTransaction(
                amount = "1,825.96",
                title = "Calc Complex",
                next = IncomeExpenseScreen(composeTestRule)
            )
    }
}