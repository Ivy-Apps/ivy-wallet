package com.ivy.wallet.compose.scenario.core

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.ItemStatisticScreen
import com.ivy.wallet.compose.component.account.AccountModal
import com.ivy.wallet.compose.component.account.AccountsTab
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.Purple1
import com.ivy.wallet.ui.theme.Purple2
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
class AccountsTest : IvyComposeTest() {

    @Test
    fun CreateAccount() = testWithRetry {
        quickOnboarding()
            .clickAccountsTab()
            .clickAddFAB()
            .enterTitle("Revolut")
            .chooseColor(Blue)
            .chooseIcon("revolut")
            .clickAdd()
            .assertAccountBalance(
                account = "Revolut",
                balance = "0",
                balanceDecimal = ".00",
                currency = "USD"
            )
    }

    @Test
    fun CreateAccount_inDifferentCurrency() = testWithRetry {
        quickOnboarding()
            .clickAccountsTab()
            .clickAddFAB()
            .enterTitle("Savings")
            .chooseColor(Purple2)
            .chooseIcon("atom")
            .chooseCurrency("EUR")
            .enterAmount("5,000.25")
            .clickAdd()
            .assertAccountBalance(
                account = "Savings",
                balance = "5,000",
                balanceDecimal = ".25",
                currency = "EUR",
                baseCurrencyEquivalent = true
            )
    }

    @Test
    fun DeleteAccount() = testWithRetry {
        quickOnboarding()
            .clickAccountsTab()
            .addAccount(
                name = "New Account",
                initialBalance = "830"
            )
            .assertAccountBalance(
                account = "New Account",
                balance = "830",
                balanceDecimal = ".00",
                currency = "USD",
                baseCurrencyEquivalent = false
            )
            .clickAccount("New Account")
            .deleteItem(next = AccountsTab(composeTestRule))
            .assertAccountNotExists(
                account = "New Account"
            )
            .clickHomeTab()
            .assertBalance(
                amount = "0",
                amountDecimal = ".00",
                currency = "USD"
            )
    }

    @Test
    fun EditAccount() = testWithRetry {
        quickOnboarding()
            .clickAccountsTab()
            .clickAccount("Bank")
            .clickEdit(AccountModal(composeTestRule))
            .enterTitle("DSK Bank")
            .chooseColor(Purple1)
            .chooseIcon("star")
            .chooseCurrency("BGN")
            .tapIncludeInBalance()
            .clickSave(next = ItemStatisticScreen(composeTestRule))
            .clickClose(next = AccountsTab(composeTestRule))
            .assertAccountBalance(
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
        quickOnboarding()
            .clickAccountsTab()
            .clickReorder()
            .clickDone()
    }
}