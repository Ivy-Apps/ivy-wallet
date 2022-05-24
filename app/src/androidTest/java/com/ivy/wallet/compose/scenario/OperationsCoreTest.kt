package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class OperationsCoreTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val accountModal = AccountModal(composeTestRule)
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val transactionFlow = TransactionFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val accountsTab = AccountsTab(composeTestRule)
    private val editTransactionScreen = TransactionScreen(composeTestRule)
    private val itemStatisticScreen = ItemStatisticScreen(composeTestRule)
    private val deleteConfirmationModal = DeleteConfirmationModal(composeTestRule)

    @Test
    fun contextLoads() {
    }

    @Test
    fun OnboardAndAdjustBalance() = testWithRetry {
        onboarding.quickOnboarding()

        composeTestRule.onNode(hasText("To accounts"))
            .performClick()


        accountsTab.clickAccount(account = "Cash")
        itemStatisticScreen.clickEdit()

        accountModal.clickBalance()
        amountInput.enterNumber("1,025.98")
        accountModal.clickSave()

        itemStatisticScreen.assertBalance(
            balance = "1,025",
            balanceDecimal = ".98",
            currency = "USD"
        )
        itemStatisticScreen.clickClose()

        mainBottomBar.clickHome()

        composeTestRule.onNodeWithTag("home_balance")
            .assertTextEquals("USD", "1,025", ".98")

        homeTab.assertBalance(
            amount = "1,025",
            amountDecimal = ".98"
        )
    }

    @Test
    fun CreateIncome() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 5000.0,
            title = "Salary",
            category = "Investments"
        )

        homeTab.dismissPrompt()

        homeTab.clickTransaction(
            amount = "5,000.00",
            title = "Salary",
            category = "Investments"
        )
    }

    @Test
    fun AddSeveralTransactions() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 1000.0,
            title = null,
            category = null
        )

        transactionFlow.addExpense(
            amount = 249.75,
            title = "Food",
            category = "Groceries"
        )

        transactionFlow.addExpense(
            amount = 300.25,
            title = null,
            category = null
        )

        homeTab.assertBalance(
            amount = "450",
            amountDecimal = ".00"
        )
    }

    @Test
    fun MakeTransfer() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 2000.0,
            account = "Bank"
        )

        transactionFlow.addTransfer(
            amount = 400.0,
            fromAccount = "Bank",
            toAccount = "Cash"
        )

        homeTab.assertBalance(
            amount = "2,000",
            amountDecimal = ".00"
        )

        mainBottomBar.clickAccounts()

        accountsTab.assertAccountBalance(
            account = "Bank",
            balance = "1,600",
            balanceDecimal = ".00"
        )

        accountsTab.assertAccountBalance(
            account = "Cash",
            balance = "400",
            balanceDecimal = ".00"
        )
    }

    @Test
    fun EditTransaction() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 20.48,
            category = "Food & Drinks",
            account = "Cash"
        )

        homeTab.dismissPrompt() //dismiss planned payments prompt because transaction card can't be clicked

        homeTab.clickTransaction(
            amount = "20.48",
            category = "Food & Drinks",
            account = "Cash"
        )

        editTransactionScreen.apply {
            editCategory(
                currentCategory = "Food & Drinks",
                newCategory = "Groceries"
            )

            editAmount(
                newAmount = "34.55"
            )

            editAccount(
                newAccount = "Bank"
            )

            editTitle(
                newTitle = "For the house"
            )

            save()
        }

        homeTab.clickTransaction(
            amount = "34.55",
            title = "For the house",
            category = "Groceries",
            account = "Bank"
        )
    }

    @Test
    fun DeleteTransaction() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 249.75,
            title = "Food",
            category = "Groceries"
        )

        homeTab.assertBalance(
            "-249",
            amountDecimal = ".75"
        )

        homeTab.dismissPrompt() //dismiss planned payments prompt because transaction card can't be clicked

        homeTab.clickTransaction(
            amount = "249.75",
            title = "Food",
            category = "Groceries"
        )

        editTransactionScreen.clickDelete()
        deleteConfirmationModal.confirmDelete()

        homeTab.assertTransactionNotExists(
            amount = "249.75"
        )

        homeTab.assertBalance(
            amount = "0",
            amountDecimal = ".00"
        )
    }

    @Test
    fun AddTransaction_withDescription() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 2178.0,
            title = "Samsung Galaxy Tab S8+",
            category = "Groceries",
            description = "Tablet for learning purposes."
        )

        homeTab.assertBalance(
            "-2,178",
            amountDecimal = ".00"
        )

        homeTab.dismissPrompt()

        homeTab.clickTransaction(
            amount = "2,178.00",
            title = "Samsung Galaxy Tab S8+",
            category = "Groceries"
        )

        editTransactionScreen.assertDescription("Tablet for learning purposes.")
    }

    @Test
    fun AddTransaction_thenRemoveDescription() = testWithRetry {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 123.0,
            title = "Income",
            description = "-a\n-b\n-c\n-d"
        )

        homeTab.assertBalance(
            "123",
            amountDecimal = ".00"
        )

        homeTab.dismissPrompt()

        homeTab.clickTransaction(
            amount = "123.00",
            title = "Income",
        )

        editTransactionScreen.assertDescription("-a\n-b\n-c\n-d")

        // No remove desc ---------------------------------------------------------------
        editTransactionScreen.removeDescription()
        editTransactionScreen.save()

        homeTab.clickTransaction(
            amount = "123.00",
            title = "Income",
        )
        editTransactionScreen.assertAddDescriptionButtonVisible()
    }
}