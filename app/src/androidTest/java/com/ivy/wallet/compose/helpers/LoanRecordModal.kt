package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule

class LoanRecordModal(
    private val composeTestRule: IvyComposeTestRule
) {
    private val amountInput = IvyAmountInput(composeTestRule)

    fun enterAmount(amount: String) {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()

        inputAmountOpenModal(amount)
    }

    fun inputAmountOpenModal(amount: String) {
        amountInput.enterNumber(amount)
    }

    fun enterNote(note: String) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(note)
    }

    fun clickAdd() {
        composeTestRule.onNodeWithText("Add")
            .performClick()
    }

    fun clickSave() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("modal_delete")
            .performClick()
    }
}