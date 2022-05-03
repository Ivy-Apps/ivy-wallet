package com.ivy.wallet.compose.scenario

import android.icu.util.Currency
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.*
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Purple1
import com.ivy.wallet.ui.theme.Purple2
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
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
    private val editTransactionScreen = TransactionScreen(composeTestRule)
    private val itemStatisticScreen = ItemStatisticScreen(composeTestRule)
    private val reorderModal = ReorderModal(composeTestRule)
    private val deleteConfirmationModal = DeleteConfirmationModal(composeTestRule)


    @Test
    fun contextLoads() {
    }

    @Test
    fun CreateAccount() = testWithRetry {
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
    fun CreateAccount_inDifferentCurrency() = testWithRetry {
        onboarding.quickOnboarding()

        mainBottomBar.clickAccounts()

        mainBottomBar.clickAddFAB()
        accountModal.apply {
            enterTitle("Savings")
            ivyColorPicker.chooseColor(Purple2)
            chooseIconFlow.chooseIcon("atom")

            chooseCurrency()
            currencyPicker.searchAndSelect(Currency.getInstance("EUR"))
            currencyPicker.modalSave()

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

    @Test
    fun DeleteAccount() = testWithRetry {
        onboarding.quickOnboarding()

        mainBottomBar.clickAccounts()

        accountsTab.addAccount(
            name = "New Account",
            initialBalance = "830"
        )

        accountsTab.assertAccountBalance(
            account = "New Account",
            balance = "830",
            balanceDecimal = ".00",
            currency = "USD",
            baseCurrencyEquivalent = false
        )

        accountsTab.clickAccount("New Account")

        itemStatisticScreen.clickDelete()
        deleteConfirmationModal.confirmDelete()

        accountsTab.assertAccountNotExists(
            account = "New Account"
        )

        mainBottomBar.clickHome()
        homeTab.assertBalance(
            amount = "0",
            amountDecimal = ".00",
            currency = "USD"
        )
    }

    @Test
    fun EditAccount() = testWithRetry {
        onboarding.quickOnboarding()

        mainBottomBar.clickAccounts()

        accountsTab.clickAccount("Bank")
        itemStatisticScreen.clickEdit()

        accountModal.apply {
            enterTitle("DSK Bank")
            ivyColorPicker.chooseColor(Purple1)
            chooseIconFlow.chooseIcon("star")

            chooseCurrency()
            currencyPicker.searchAndSelect(Currency.getInstance("BGN"))
            currencyPicker.modalSave()

            tapIncludeInBalance()

            clickSave()
        }

        itemStatisticScreen.clickClose()

        accountsTab.assertAccountBalance(
            account = "DSK Bank",
            balance = "0",
            balanceDecimal = ".00",
            currency = "BGN",
            baseCurrencyEquivalent = true
        )
    }

    /**
     * semiTest because no actual reordering is being gone
     */
    @Ignore("Fails with very weird: java.lang.String com.ivy.wallet.domain.Settings.getCurrency()' on a null object reference")
    @Test
    fun ReorderAccounts_semiTest() = testWithRetry {
        onboarding.quickOnboarding()
        mainBottomBar.clickAccounts()

        accountsTab.clickReorder()
        reorderModal.clickDone()
    }
}