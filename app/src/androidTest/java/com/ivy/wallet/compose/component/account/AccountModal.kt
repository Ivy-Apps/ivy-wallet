package com.ivy.wallet.compose.component.account

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.amountinput.AmountInput
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput
import com.ivy.wallet.compose.component.picker.*

class AccountModal(
    private val composeTestRule: IvyComposeTestRule
) : ColorPicker<AccountModal>, IconPicker<AccountModal>,
    CurrencyPicker<AccountModal>, AmountInput<AccountModal> {

    fun enterTitle(
        title: String
    ): AccountModal {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(title)
        return this
    }

    private fun clickBalance(): IvyAmountInput {
        composeTestRule
            .onNode(hasTestTag("amount_balance"))
            .performClick()
        return IvyAmountInput(composeTestRule)
    }

    private fun clickCurrency(): IvyCurrencyPicker {
        composeTestRule.onNodeWithTag("account_modal_currency")
            .performClick()
        return IvyCurrencyPicker(composeTestRule)
    }

    fun <N> clickSave(next: N): N {
        composeTestRule
            .onNode(hasText("Save"))
            .performClick()
        return next
    }

    fun clickAdd(): AccountsTab {
        composeTestRule
            .onNode(hasText("Add"))
            .performClick()
        return AccountsTab(composeTestRule)
    }

    fun tapIncludeInBalance(): AccountModal {
        composeTestRule.onNodeWithText("Include account")
            .performClick()
        return this
    }

    override fun chooseColor(color: Color): AccountModal {
        IvyColorPicker(composeTestRule).chooseColor(color)
        return this
    }

    override fun chooseIcon(icon: String): AccountModal {
        IvyIconPicker(composeTestRule).chooseIcon(icon)
        return this
    }


    override fun chooseCurrency(currencyCode: String): AccountModal {
        clickCurrency()
            .chooseCurrency(currencyCode)
        return this
    }

    override fun enterAmount(number: String): AccountModal {
        return clickBalance()
            .enterNumber(
                number = number,
                next = this
            )
    }
}