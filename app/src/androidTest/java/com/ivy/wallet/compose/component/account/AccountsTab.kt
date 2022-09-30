package com.ivy.wallet.compose.component.account

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.design.l0_system.color.Purple
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.ItemStatisticScreen
import com.ivy.wallet.compose.component.ReorderModal
import com.ivy.wallet.compose.component.home.MainBottomBar
import com.ivy.wallet.compose.hideKeyboard
import com.ivy.wallet.compose.util.printTree

class AccountsTab(
    private val composeTestRule: IvyComposeTestRule
) : MainBottomBar<AccountModal>(composeTestRule) {

    fun assertAccountBalance(
        account: String,
        balance: String,
        balanceDecimal: String,
        currency: String = "USD",
        baseCurrencyEquivalent: Boolean = false
    ): AccountsTab {
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

        return this
    }

    fun clickAccount(
        account: String
    ): ItemStatisticScreen {
        composeTestRule.onNode(hasText(account)).performClick()
        return ItemStatisticScreen(composeTestRule)
    }

    fun assertAccountNotExists(
        account: String
    ): AccountsTab {
        composeTestRule.onNode(hasText(account)).assertDoesNotExist()
        return this
    }

    fun addAccount(
        name: String,
        color: Color = Purple,
        icon: String? = null,
        currency: String? = null,
        initialBalance: String? = null
    ): AccountsTab = clickAddFAB(next = AccountModal(composeTestRule))
        .enterTitle(name)
        .apply {
            composeTestRule.hideKeyboard()
        }.chooseColor(color = color)
        .apply {
            if (icon != null) {
                chooseIcon(icon)
            }
        }
        .apply {
            if (currency != null) {
                chooseCurrency(currency)
            }
        }.apply {
            if (initialBalance != null) {
                enterAmount(initialBalance)
            }
        }.clickAdd()

    fun clickReorder(): ReorderModal {
        composeTestRule.onNodeWithTag("reorder_button")
            .performClick()
        return ReorderModal(composeTestRule)
    }

    override fun clickAddFAB(): AccountModal {
        return clickAddFAB(next = AccountModal(composeTestRule))
    }
}