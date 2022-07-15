package com.ivy.wallet.compose.scenario.core

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.data.planned.IntervalType
import com.ivy.data.transaction.TransactionType
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.account.AccountsTab
import com.ivy.wallet.compose.component.edittrn.screen.IncomeExpenseScreen
import com.ivy.wallet.compose.component.home.HomeTab
import com.ivy.wallet.compose.component.planned.EditPlannedScreen
import com.ivy.wallet.compose.component.planned.PlannedPaymentsScreen
import com.ivy.wallet.utils.timeNowUTC
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class PlannedPaymentsTest : IvyComposeTest() {

    @Test
    fun Onboard_CreatePlannedPaymentFromPrompt() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 100.0
            )

        //"Add Planned Payment" prompt
        composeTestRule.onNodeWithTag("cta_prompt_add_planned_payment")
            .performClick()

        EditPlannedScreen(composeTestRule)
            .addPlannedPayment(
                type = TransactionType.INCOME,
                oneTime = false,
                amount = "2,000",
                category = "Bills & Fees",
                startDate = timeNowUTC().withDayOfMonth(1),
                intervalN = 1,
                intervalType = IntervalType.MONTH,
                title = "Salary",

                next = HomeTab(composeTestRule)
            )
            .assertUpcomingIncome(
                amount = "2,000.00",
                currency = "USD"
            )
            .clickUpcoming()
            .clickTransaction(
                amount = "2,000.00",
                category = "Bills & Fees",
                title = "Salary",
                next = IncomeExpenseScreen(composeTestRule)
            )
            .clickGet(next = HomeTab(composeTestRule))
            .assertBalance(
                amount = "2,100",
                amountDecimal = ".00",
                currency = "USD"
            )
    }

    @Test
    fun CreateOneTimePlannedPayment_fromFAB() = testWithRetry {
        quickOnboarding()
            //Add one transaction so the "Adjust Balance" prompt can disappear and the screen to be scrollable
            .addIncome(
                amount = 100.0,
                title = "Adjust Balance"
            )
            .dismissPrompt() //Dismiss "Add Planned Payment" prompt
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "530.25",
                category = "Transport",
                startDate = null,
                intervalN = null,
                intervalType = null,
                title = "Netherlands airplane",

                next = HomeTab(composeTestRule)
            )
            .assertBalance(
                amount = "100",
                amountDecimal = ".00"
            )
            .assertUpcomingExpense(
                amount = "530.25",
                currency = "USD"
            )
            .clickUpcoming()
            .clickTransactionPay()
            .assertBalance(
                amount = "-430", //-530.25 + 100.00 = -430.25
                amountDecimal = ".25"
            )
    }

    @Test
    fun DeletePlannedPayment_Instance() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.EXPENSE,
                oneTime = false,
                amount = "650",
                category = "Bills & Fees",
                startDate = null,
                intervalN = 1,
                intervalType = IntervalType.MONTH,
                title = "Rent",

                next = HomeTab(composeTestRule)
            )
            .assertUpcomingExpense(
                amount = "650.00",
                currency = "USD"
            )
            .clickUpcoming()
            .clickTransaction(
                amount = "650.00",
                category = "Bills & Fees",
                title = "Rent",
                next = IncomeExpenseScreen(composeTestRule)
            )
            .clickDelete()
            .confirmDelete(next = HomeTab(composeTestRule))
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
            .assertUpcomingDoesNotExist()
            //Assert that the root planned payment exists
            .openMoreMenu()
            .clickPlannedPayments()
            .clickPlannedPayment(
                amount = "650.00"
            )
    }

    @Test
    fun DeletePlannedPayment_Root() = testWithRetry {
        quickOnboarding()
            .openMoreMenu()
            .clickPlannedPayments()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.EXPENSE,
                oneTime = false,
                amount = "650",
                category = "Bills & Fees",
                startDate = null,
                intervalN = 1,
                intervalType = IntervalType.MONTH,
                title = "Rent",

                next = PlannedPaymentsScreen(composeTestRule)
            )
            .assertUpcomingExpense(
                amount = "650.00",
                currency = "USD"
            )
            .clickPlannedPayment(
                amount = "650.00"
            )
            .clickDelete()
            .confirmDelete(next = PlannedPaymentsScreen(composeTestRule))

            .assertPlannedPaymentDoesNotExist(
                amount = "650.00"
            )
            .clickClose()
            .closeMoreMenu()
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
            .assertUpcomingDoesNotExist()
    }

    @Test
    fun AddSeveralPlannedPayments() = testWithRetry {
        quickOnboarding()
            //Add recurring INCOME
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.INCOME,
                oneTime = false,
                amount = "5,250.33",
                category = "Bills & Fees",
                startDate = timeNowUTC().withDayOfMonth(1),
                intervalN = 31,
                intervalType = IntervalType.DAY,
                title = "Salary",

                next = HomeTab(composeTestRule)
            )
            .assertUpcomingIncome(
                amount = "5,250.33",
                currency = "USD"
            )
            //Add recurring EXPENSE
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.EXPENSE,
                oneTime = false,
                amount = "12.99",
                category = "Entertainment",
                startDate = timeNowUTC().withDayOfMonth(1),
                intervalN = 1,
                intervalType = IntervalType.MONTH,
                title = "Netflix",

                next = HomeTab(composeTestRule)
            )
            .assertUpcomingExpense(
                amount = "12.99",
                currency = "USD"
            )
            //Add one-time EXPENSE
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "2,000",
                category = "Transport",
                startDate = timeNowUTC().withDayOfMonth(1),
                intervalN = null,
                intervalType = null,
                title = "Vacation",

                next = HomeTab(composeTestRule)
            )
            .assertUpcomingExpense(
                amount = "2,012.99", //2,000 + 12.99
                currency = "USD"
            )
            .assertUpcomingIncome(
                amount = "5,250.33",
                currency = "USD"
            )
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
    }

    @Test
    fun skipPlannedPaymentsOnHomeTab() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                next = HomeTab(composeTestRule),
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "530.25",
                category = "Transport",
                startDate = null,
                intervalN = null,
                intervalType = null,
                title = "Netherlands airplane"
            )
            .clickUpcoming()
            .clickTransactionSkip()
            .assertUpcomingDoesNotExist()
    }

    @Test
    fun payPlannedPaymentsOnHomeTab() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                next = HomeTab(composeTestRule),
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "530.25",
                category = "Transport",
                startDate = null,
                intervalN = null,
                intervalType = null,
                title = "Netherlands airplane"
            )
            .clickUpcoming()
            .clickTransactionPay()
            .assertUpcomingDoesNotExist()
            .assertBalance(
                amount = "-530",
                amountDecimal = ".25"
            )
    }

    @Test
    fun skipPlannedPaymentsOnItemStatistics() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                next = HomeTab(composeTestRule),
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "530.25",
                category = "Transport",
                startDate = null,
                intervalN = null,
                intervalType = null,
                title = "Netherlands airplane"
            )
            .clickAccountsTab()
            .clickAccount("Cash")
            .clickUpcoming()
            .clickTransactionSkip()
            .clickClose(AccountsTab(composeTestRule))
            .clickHomeTab()
            .assertUpcomingDoesNotExist()
    }

    @Test
    fun payPlannedPaymentsOnItemStatistics() = testWithRetry {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .addPlannedPayment(
                next = HomeTab(composeTestRule),
                type = TransactionType.EXPENSE,
                oneTime = true,
                amount = "530.25",
                category = "Transport",
                startDate = null,
                intervalN = null,
                intervalType = null,
                title = "Netherlands airplane"
            )
            .clickAccountsTab()
            .clickAccount("Cash")
            .clickUpcoming()
            .clickTransactionPay()
            .clickClose(AccountsTab(composeTestRule))
            .clickHomeTab()
            .assertUpcomingDoesNotExist()
            .assertBalance(
                amount = "-530",
                amountDecimal = ".25"
            )
    }
}