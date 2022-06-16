package com.ivy.wallet.compose.component.edittrn.screen

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class TransferScreen(
    composeTestRule: IvyComposeTestRule
) : TransactionScreen(composeTestRule) {
    fun selectFromAccount(
        fromAccount: String
    ): TransferScreen {
        composeTestRule.onNode(
            hasTestTag("from_account")
                .and(hasText(fromAccount))
        ).performClick()
        return this
    }

    fun selectToAccount(
        toAccount: String
    ): TransferScreen {
        composeTestRule.onNode(
            hasTestTag("to_account")
                .and(hasText(toAccount))
        ).performClick()

        return this
    }
}