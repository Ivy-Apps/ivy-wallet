package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.hideKeyboard

open class TransactionScreen(
    protected val composeTestRule: IvyComposeTestRule
) {
    fun editAmount(
        newAmount: String
    ): TransferScreen {
        return clickAmount()
            .enterNumber(newAmount, next = TransferScreen(composeTestRule))
    }

    fun clickAmount(): IvyAmountInput {
        composeTestRule.onNodeWithTag("edit_amount_balance_row")
            .performClick()
        return IvyAmountInput(composeTestRule)
    }

    fun editAccount(
        newAccount: String
    ): TransactionScreen {
        composeTestRule.onNode(
            hasTestTag("from_account")
                .and(hasText(newAccount))
        ).performClick()

        return this
    }

    fun editTitle(
        newTitle: String
    ) {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(newTitle)
    }

    fun removeDescription() {
        composeTestRule.hideKeyboard()

        composeTestRule.onNodeWithTag("trn_description")
            .performClick()

        composeTestRule.onNodeWithTag("modal_desc_delete")
            .performClick()
    }

    fun assertDescription(desc: String) {
        composeTestRule.onNodeWithTag("trn_description", useUnmergedTree = true)
            .assertTextEquals(desc)
    }

    fun assertAddDescriptionButtonVisible() {
        composeTestRule.onNodeWithText("Add description")
            .assertIsDisplayed()
    }

    fun clickClose() {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
    }

    fun save() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }

    fun clickAdd() {
        composeTestRule.onNodeWithText("Add")
            .performClick()
    }

    fun skipCategory() {
        composeTestRule.onNodeWithText("Skip")
            .performClick()
    }

    fun firstOpen(): TransactionAmountInput = TransactionAmountInput(composeTestRule)

    fun addDescription(description: String): TransactionScreen {
        composeTestRule.hideKeyboard()

        composeTestRule.onNodeWithText("Add description")
            .performClick()

        composeTestRule.onNode(
            hasTestTag("modal_desc_input"),
            useUnmergedTree = true
        ).performTextReplacement(description)

        composeTestRule.onNodeWithTag("modal_desc_save")
            .performClick()

        return this
    }
}