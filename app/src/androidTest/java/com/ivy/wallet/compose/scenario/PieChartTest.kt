package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.HomeTab
import com.ivy.wallet.compose.helpers.OnboardingFlow
import com.ivy.wallet.compose.helpers.PieChartScreen
import com.ivy.wallet.compose.helpers.TransactionFlow
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class PieChartTest : IvyComposeTest() {
    private val onboarding = OnboardingFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val transactionFlow = TransactionFlow(composeTestRule)
    private val pieChartScreen = PieChartScreen(composeTestRule)

    @Test
    fun expensePieChart_realistic() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 50.23
        )

        transactionFlow.addExpense(
            amount = 150.72,
            category = "Food & Drinks"
        )

        transactionFlow.addExpense(
            amount = 75.0,
            category = "Groceries"
        )

        transactionFlow.addExpense(
            amount = 5.0,
            title = "Bread",
            category = "Groceries"
        )
        //----------------------------------------------------

        homeTab.clickExpenseCard()

        pieChartScreen.assertTitle("Expenses")
        pieChartScreen.assertTotalAmount(
            amountInt = "280",
            decimalPart = ".95",
            currency = "USD"
        )
    }

    @Test
    fun expensePieChart_empty() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 23.23
        )

        //----------------------------------------------------

        homeTab.clickExpenseCard()

        pieChartScreen.assertTitle("Expenses")
        pieChartScreen.assertTotalAmount(
            amountInt = "0",
            decimalPart = ".00",
            currency = "USD"
        )
    }

    @Test
    fun expensePieChart_oneTrn() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 55.01
        )

        //----------------------------------------------------

        homeTab.clickExpenseCard()

        pieChartScreen.assertTitle("Expenses")
        pieChartScreen.assertTotalAmount(
            amountInt = "55",
            decimalPart = ".01",
            currency = "USD"
        )
    }

    @Test
    fun incomePieChart_realistic() = testWithRetry {
        onboarding.quickOnboarding()

        //To ensure that the code filters expenses
        transactionFlow.addExpense(
            amount = 10.0
        )

        transactionFlow.addIncome(
            amount = 7200.0,
            title = "Salary",
            category = "Groceries"
        )

        transactionFlow.addIncome(
            amount = 1.1,
            title = "Adjust balance"
        )

        //----------------------------------------------------

        homeTab.clickIncomeCard()

        pieChartScreen.assertTitle("Income")
        pieChartScreen.assertTotalAmount(
            amountInt = "7,201",
            decimalPart = ".10",
            currency = "USD"
        )
    }

    @Test
    fun incomePieChart_empty() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 23.23
        )

        //----------------------------------------------------

        homeTab.clickIncomeCard()

        pieChartScreen.assertTitle("Income")
        pieChartScreen.assertTotalAmount(
            amountInt = "0",
            decimalPart = ".00",
            currency = "USD"
        )
    }
}