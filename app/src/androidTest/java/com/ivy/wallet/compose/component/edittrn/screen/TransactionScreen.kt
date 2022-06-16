package com.ivy.wallet.compose.component.edittrn.screen

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DeleteConfirmationModal
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput
import com.ivy.wallet.compose.component.edittrn.TransactionAmountInput
import com.ivy.wallet.compose.hideKeyboard

abstract class TransactionScreen(
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
    ): TransactionScreen {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(newTitle)
        return this
    }

    fun removeDescription(): TransactionScreen {
        composeTestRule.hideKeyboard()

        composeTestRule.onNodeWithTag("trn_description")
            .performClick()

        composeTestRule.onNodeWithTag("modal_desc_delete")
            .performClick()
        return this
    }

    fun assertDescription(desc: String): TransactionScreen {
        composeTestRule.onNodeWithTag("trn_description", useUnmergedTree = true)
            .assertTextEquals(desc)
        return this
    }

    fun assertAddDescriptionButtonVisible(): TransactionScreen {
        composeTestRule.onNodeWithText("Add description")
            .assertIsDisplayed()
        return this
    }

    fun <N> clickClose(next: N): N {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()
        return next
    }

    fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    fun <N> save(next: N): N {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return next
    }

    fun <N> clickAdd(next: N): N {
        composeTestRule.onNodeWithText("Add")
            .performClick()
        return next
    }

    fun skipCategory(): TransactionScreen {
        composeTestRule.onNodeWithText("Skip")
            .performClick()
        return this
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

    fun <N> clickGet(next: N): N {
        composeTestRule.onNodeWithText("Get")
            .performClick()
        return next

    }

    fun <N> clickPay(next: N): N {
        composeTestRule.onNodeWithText("Pay")
            .performClick()
        return next
    }

}