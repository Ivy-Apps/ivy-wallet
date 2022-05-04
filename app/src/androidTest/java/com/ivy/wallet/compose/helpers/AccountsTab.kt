package com.ivy.wallet.compose.helpers

import android.icu.util.Currency
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.compose.hideKeyboard
import com.ivy.wallet.compose.printTree
import com.ivy.wallet.ui.theme.Ivy

class AccountsTab<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val mainBottomBar = MainBottomBar(composeTestRule)
    private val accountModal = AccountModal(composeTestRule)
    private val amountInput = AmountInput(composeTestRule)

    fun assertAccountBalance(
        account: String,
        balance: String,
        balanceDecimal: String,
        currency: String = "USD",
        baseCurrencyEquivalent: Boolean = false
    ) {
        composeTestRule.printTree()

        composeTestRule.onNode(
            hasText(account)
                .and(hasAnyDescendant(hasTextExactly(balance, balanceDecimal, currency)))
        ).assertExists()

        val baseCurrencyBalanceRow = composeTestRule.onNode(
            hasText(account)
                .and(hasAnyDescendant(hasTestTag("baseCurrencyEquivalent")))
        )
        if (baseCurrencyEquivalent) {
            baseCurrencyBalanceRow.assertIsDisplayed()
        } else {
            baseCurrencyBalanceRow.assertDoesNotExist()
        }
    }

    fun clickAccount(
        account: String
    ) {
        composeTestRule.onNode(hasText(account)).performClick()
    }

    fun assertAccountNotExists(
        account: String
    ) {
        composeTestRule.onNode(hasText(account)).assertDoesNotExist()
    }

    fun addAccount(
        name: String,
        color: Color = Ivy,
        icon: String? = null,
        currency: String? = null,
        initialBalance: String? = null
    ) {
        mainBottomBar.clickAddFAB()

        accountModal.apply {
            enterTitle(name)

            composeTestRule.hideKeyboard()

            ivyColorPicker.chooseColor(color = color)

            if (icon != null) {
                chooseIconFlow.chooseIcon(icon)
            }

            if (currency != null) {
                chooseCurrency()
                currencyPicker.searchAndSelect(Currency.getInstance(currency))
                currencyPicker.modalSave()
            }


            if (initialBalance != null) {
                clickBalance()
                amountInput.enterNumber(initialBalance)
            }

            clickAdd()
        }
    }

    fun clickReorder() {
        composeTestRule.onNodeWithTag("reorder_button")
            .performClick()
    }
}