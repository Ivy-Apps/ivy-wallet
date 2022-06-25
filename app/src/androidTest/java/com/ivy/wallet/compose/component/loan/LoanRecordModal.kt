package com.ivy.wallet.compose.component.loan

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DeleteConfirmationModal
import com.ivy.wallet.compose.component.DeleteItem
import com.ivy.wallet.compose.component.amountinput.AmountInput
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput

class LoanRecordModal(
    private val composeTestRule: IvyComposeTestRule
) : AmountInput<LoanRecordModal>, DeleteItem<LoanDetailsScreen> {

    override fun enterAmount(amount: String): LoanRecordModal {
        return clickLoanAmount()
            .enterNumber(number = amount, next = LoanRecordModal(composeTestRule))
    }

    private fun clickLoanAmount(): IvyAmountInput {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()
        return IvyAmountInput(composeTestRule)
    }

    fun enterAmountWhenAmountInputOpened(amount: String): LoanRecordModal {
        return firstOpenAddNew()
            .enterNumber(number = amount, next = this)
    }

    private fun firstOpenAddNew(): IvyAmountInput {
        return IvyAmountInput(composeTestRule)
    }

    fun enterNote(note: String): LoanRecordModal {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(note)
        return this
    }

    fun clickAdd(): LoanDetailsScreen {
        composeTestRule.onNodeWithText("Add")
            .performClick()
        return LoanDetailsScreen(composeTestRule)
    }

    fun clickSave(): LoanDetailsScreen {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return LoanDetailsScreen(composeTestRule)
    }

    private fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("modal_delete")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    override fun deleteWithConfirmation(): LoanDetailsScreen {
        return clickDelete()
            .confirmDelete(next = LoanDetailsScreen(composeTestRule))
    }

    fun clickClose(): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("modal_close_button")
            .performClick()
        return LoanDetailsScreen(composeTestRule)
    }
}