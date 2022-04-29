package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.HomeTab
import com.ivy.wallet.compose.helpers.OnboardingFlow
import com.ivy.wallet.compose.helpers.PieChartScreen
import com.ivy.wallet.compose.helpers.TransactionFlow
import com.ivy.wallet.compose.printTree
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class PieChartTest : IvyComposeTest() {
    private val onboarding = OnboardingFlow(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)
    private val transactionFlow = TransactionFlow(composeTestRule)
    private val pieChartScreen = PieChartScreen(composeTestRule)

    @Test
    fun expensePieChart_realistic() {
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

        composeTestRule.printTree(useUnmergedTree = false)

        pieChartScreen.assertTitle("Expenses")
        pieChartScreen.assertTotalAmount(
            amountInt = "280",
            decimalPart = ".95",
            currency = "USD"
        )
    }
}