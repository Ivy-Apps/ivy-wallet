package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class PieChartTest : IvyComposeTest() {

    @Test
    fun expensePieChart_realistic() = testWithRetry {
        quickOnboarding()
            .addExpense(
                amount = 50.23
            )
            .addExpense(
                amount = 150.72,
                category = "Food & Drinks"
            )
            .addExpense(
                amount = 75.0,
                category = "Groceries"
            )
            .addExpense(
                amount = 5.0,
                title = "Bread",
                category = "Groceries"
            )
            //----------------------------------------------------
            .clickExpenseCard()
            .assertTitle("Expenses")
            .assertTotalAmount(
                amountInt = "280",
                decimalPart = ".95",
                currency = "USD"
            )
    }

    @Test
    fun expensePieChart_empty() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 23.23
            )
            //----------------------------------------------------
            .clickExpenseCard()
            .assertTitle("Expenses")
            .assertTotalAmount(
                amountInt = "0",
                decimalPart = ".00",
                currency = "USD"
            )
    }

    @Test
    fun expensePieChart_oneTrn() = testWithRetry {
        quickOnboarding()
            .addExpense(
                amount = 55.01
            )
            //----------------------------------------------------
            .clickExpenseCard()
            .assertTitle("Expenses")
            .assertTotalAmount(
                amountInt = "55",
                decimalPart = ".01",
                currency = "USD"
            )
    }

    @Test
    fun incomePieChart_realistic() = testWithRetry {
        quickOnboarding()
            //To ensure that the code filters expenses
            .addExpense(
                amount = 10.0
            )
            .addIncome(
                amount = 7200.0,
                title = "Salary",
                category = "Groceries"
            )
            .addIncome(
                amount = 1.1,
                title = "Adjust balance"
            )
            //----------------------------------------------------
            .clickIncomeCard()
            .assertTitle("Income")
            .assertTotalAmount(
                amountInt = "7,201",
                decimalPart = ".10",
                currency = "USD"
            )
    }

    @Test
    fun incomePieChart_empty() = testWithRetry {
        quickOnboarding()
            .addExpense(
                amount = 23.23
            )
            //----------------------------------------------------
            .clickIncomeCard()
            .assertTitle("Income")
            .assertTotalAmount(
                amountInt = "0",
                decimalPart = ".00",
                currency = "USD"
            )
    }
}