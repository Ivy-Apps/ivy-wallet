package com.ivy.wallet.compose.scenario

import android.icu.util.Currency
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Purple2
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

    @Test
    fun CreateAccount_inDifferentCurrency() {
        onboarding.quickOnboarding()

        mainBottomBar.clickAccounts()
        mainBottomBar.clickAddFAB()

        accountModal.apply {
            enterTitle("Savings")
            ivyColorPicker.chooseColor(Purple2)
            chooseIconFlow.chooseIcon("atom")

            chooseCurrency()
            currencyPicker.searchAndSelect(Currency.getInstance("EUR"))

            clickBalance()
            amountInput.enterNumber("5,000.25")

            clickAdd()
        }

        accountsTab.assertAccountBalance(
            account = "Savings",
            balance = "5,000",
            balanceDecimal = ".25",
            currency = "EUR",
            baseCurrencyEquivalent = true
        )
    }
}