package com.ivy.wallet.compose.component

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.util.scrollNextUntilFound

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
            .performScrollTo()
            .performClick()
        return next
    }

    fun clickUpcoming(): ItemStatisticScreen {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).performClick()
        return this
    }

    fun clickTransactionSkip(): ItemStatisticScreen =
        scrollNextUntilFound(itemStatsLazyColumn()) {
            composeTestRule.onNode(
                hasText("Skip")
                    .and(hasAnyAncestor(hasTestTag("transaction_card")))
            )
                .performScrollTo()
                .performClick()

            this
        }

    fun clickTransactionPay(): ItemStatisticScreen =
        scrollNextUntilFound(itemStatsLazyColumn()) {
            composeTestRule.onNode(
                hasText("Pay")
                    .and(hasAnyAncestor(hasTestTag("transaction_card")))
            )
                .performScrollTo()
                .performClick()

            this
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

    fun itemStatsLazyColumn() = composeTestRule.onNodeWithTag("item_stats_lazy_column")
}