package com.ivy.wallet.compose.component.loan

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.amountinput.AmountInput
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput
import com.ivy.wallet.compose.component.picker.ColorPicker
import com.ivy.wallet.compose.component.picker.IconPicker
import com.ivy.wallet.compose.component.picker.IvyColorPicker
import com.ivy.wallet.compose.component.picker.IvyIconPicker
import com.ivy.wallet.domain.data.LoanType

class LoanModal(
    private val composeTestRule: IvyComposeTestRule
) : AmountInput<LoanModal>, ColorPicker<LoanModal>, IconPicker<LoanModal> {
    fun enterName(loanName: String): LoanModal {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(loanName)
        return this
    }

    fun selectLoanType(loanType: LoanType): LoanModal {
        when (loanType) {
            LoanType.BORROW -> {
                composeTestRule.onNodeWithText("Borrow money")
                    .performClick()
            }
            LoanType.LEND -> {
                composeTestRule.onNodeWithText("Lend money")
                    .performClick()
            }
        }
        return this
    }

    private fun clickLoanAmount(): IvyAmountInput {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()
        return IvyAmountInput(composeTestRule)
    }

    fun clickAdd(): LoansScreen {
        composeTestRule.onNodeWithText("Add")
            .performClick()
        return LoansScreen(composeTestRule)
    }

    fun <N> clickSave(next: N): N {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return next
    }

    fun selectDefaultLoanAccount(): LoanModal {
        composeTestRule.onNode(hasText("Cash")).performClick()
        return this
    }

    override fun chooseIcon(icon: String): LoanModal {
        IvyIconPicker(composeTestRule).chooseIcon(icon)
        return this
    }

    override fun chooseColor(color: Color): LoanModal {
        IvyColorPicker(composeTestRule).chooseColor(color)
        return this
    }

    override fun enterAmount(number: String): LoanModal {
        return clickLoanAmount()
            .enterNumber(number = number, next = this)
    }
}