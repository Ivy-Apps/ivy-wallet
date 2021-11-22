package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class BasicOperationsTest : IvyComposeTest() {

    private val onboarding = OnboardingFlow(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)
    private val accountModal = AccountModal(composeTestRule)
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val transactionFlow = TransactionFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val accountsTab = AccountsTab(composeTestRule)
    private val editTransactionScreen = EditTransactionScreen(composeTestRule)


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

        homeTab.assertBalance(
            amount = "1,025",
            amountDecimal = ".98"
        )
    }

    @Test
    fun CreateIncome() {
        onboarding.quickOnboarding()

        transactionFlow.addIncome(
            amount = 5000.0,
            title = "Salary",
            category = "Investments"
        )

        composeTestRule.onNodeWithTag("transaction_card")
            .assertIsDisplayed()
    }

    @Test
    fun AddSeveralTransactions() {
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
    fun MakeTransfer() {
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
    fun EditTransaction() {
        onboarding.quickOnboarding()

        transactionFlow.addExpense(
            amount = 20.48,
            category = "Food & Drinks",
            account = "Cash"
        )

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
}