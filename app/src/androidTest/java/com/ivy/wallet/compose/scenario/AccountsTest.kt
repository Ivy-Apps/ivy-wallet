package com.ivy.wallet.compose.scenario

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.ui.theme.Blue
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class AccountsTest : IvyComposeTest() {

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
    fun CreateAccount() {
        onboarding.quickOnboarding()

        mainBottomBar.clickAccounts()
        mainBottomBar.clickAddFAB()

        accountModal.apply {
            enterTitle("Revolut")
            ivyColorPicker.chooseColor(Blue)
            chooseIconFlow.chooseIcon("revolut")
            clickAdd()
        }

        accountsTab.assertAccountBalance(
            account = "Revolut",
            balance = "0",
            balanceDecimal = ".00",
            currency = "USD"
        )
    }
}