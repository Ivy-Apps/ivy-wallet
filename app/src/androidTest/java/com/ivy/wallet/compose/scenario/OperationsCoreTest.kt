package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OperationsCoreTest : IvyComposeTest() {

    @Test
    fun OnboardAndAdjustBalance() = testWithRetry {
        quickOnboarding()

        //Adjust Balance prompt
        composeTestRule.onNode(hasText("To accounts"))
            .performClick()


        AccountsTab(composeTestRule)
            .clickAccount(account = "Cash")
            .clickEdit(next = AccountModal(composeTestRule))
            .enterAmount("1,025.98")
            .clickSave(next = ItemStatisticScreen(composeTestRule))
            .assertBalance(
                balance = "1,025",
                balanceDecimal = ".98",
                currency = "USD"
            )
            .clickClose(next = AccountsTab(composeTestRule))
            .clickHomeTab()
            .assertBalance(
                amount = "1,025",
                amountDecimal = ".98",
                currency = "USD"
            )
    }

    @Test
    fun CreateIncome() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 5000.0,
                title = "Salary",
                category = "Investments"
            )
            .dismissPrompt()
            .clickTransaction(
                amount = "5,000.00",
                title = "Salary",
                category = "Investments"
            )
    }

    @Test
    fun AddSeveralTransactions() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 1000.0,
                title = null,
                category = null
            )
            .addExpense(
                amount = 249.75,
                title = "Food",
                category = "Groceries"
            )
            .addExpense(
                amount = 300.25,
                title = null,
                category = null
            )
            .assertBalance(
                amount = "450",
                amountDecimal = ".00"
            )
    }

    @Test
    fun MakeTransfer() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 2000.0,
                account = "Bank"
            )
            .addTransfer(
                amount = 400.0,
                fromAccount = "Bank",
                toAccount = "Cash"
            )
            .assertBalance(
                amount = "2,000",
                amountDecimal = ".00"
            )
            .clickAccountsTab()
            .assertAccountBalance(
                account = "Bank",
                balance = "1,600",
                balanceDecimal = ".00"
            )
            .assertAccountBalance(
                account = "Cash",
                balance = "400",
                balanceDecimal = ".00"
            )
    }

    @Test
    fun EditTransaction() = testWithRetry {
        val part1 = quickOnboarding()
            .addExpense(
                amount = 20.48,
                category = "Food & Drinks",
                account = "Cash"
            )
            .dismissPrompt() //dismiss planned payments prompt because transaction card can't be clicked
            .clickTransaction(
                amount = "20.48",
                category = "Food & Drinks",
                account = "Cash"
            ) as IncomeExpenseScreen

        part1.editCategory(
            currentCategory = "Food & Drinks",
            newCategory = "Groceries"
        ).editAmount(
            newAmount = "34.55"
        ).editAccount(
            newAccount = "Bank"
        ).editTitle(
            newTitle = "For the house"
        ).save(next = HomeTab(composeTestRule))
            .clickTransaction(
                amount = "34.55",
                title = "For the house",
                category = "Groceries",
                account = "Bank"
            )
    }

    @Test
    fun DeleteTransaction() = testWithRetry {
        quickOnboarding()
            .addExpense(
                amount = 249.75,
                title = "Food",
                category = "Groceries"
            )
            .assertBalance(
                "-249",
                amountDecimal = ".75"
            )
            .dismissPrompt() //dismiss planned payments prompt because transaction card can't be clicked
            .clickTransaction(
                amount = "249.75",
                title = "Food",
                category = "Groceries"
            )
            .clickDelete()
            .confirmDelete(next = HomeTab(composeTestRule))
            .assertTransactionNotExists(
                amount = "249.75"
            )
            .assertBalance(
                amount = "0",
                amountDecimal = ".00"
            )
    }

    @Test
    fun AddTransaction_withDescription() = testWithRetry {
        quickOnboarding()
            .addExpense(
                amount = 2178.0,
                title = "Samsung Galaxy Tab S8+",
                category = "Groceries",
                description = "Tablet for learning purposes."
            )
            .assertBalance(
                "-2,178",
                amountDecimal = ".00"
            )
            .dismissPrompt()
            .clickTransaction(
                amount = "2,178.00",
                title = "Samsung Galaxy Tab S8+",
                category = "Groceries"
            )
            .assertDescription("Tablet for learning purposes.")
    }

    @Test
    fun AddTransaction_thenRemoveDescription() = testWithRetry {
        quickOnboarding()
            .addIncome(
                amount = 123.0,
                title = "Income",
                description = "-a\n-b\n-c\n-d"
            )
            .assertBalance(
                "123",
                amountDecimal = ".00"
            )
            .dismissPrompt()
            .clickTransaction(
                amount = "123.00",
                title = "Income",
            )
            .assertDescription("-a\n-b\n-c\n-d")
            // No remove desc ---------------------------------------------------------------
            .removeDescription()
            .save(next = HomeTab(composeTestRule))
            .clickTransaction(
                amount = "123.00",
                title = "Income",
            )
            .assertAddDescriptionButtonVisible()
    }
}