package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

open class ItemStatisticScreen(
    protected val composeTestRule: IvyComposeTestRule
) {

    private fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    fun <N> clickEdit(next: N): N {
        composeTestRule.onNodeWithText("Edit")
            .performClick()
        return next
    }

    fun <N> clickClose(next: N): N {
        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()
        return next
    }

    fun assertBalance(
        balance: String,
        balanceDecimal: String,
        currency: String
    ): ItemStatisticScreen {
        composeTestRule.onNodeWithTag("balance")
            .assertTextEquals(currency, balance, balanceDecimal)
        return this
    }

    fun <T> deleteItem(next: T): T {
        return clickDelete()
            .confirmDelete(next = next)
    }
}