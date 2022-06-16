package com.ivy.wallet.compose.component.edittrn

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput

open class TransactionAmountInput(
    composeTestRule: IvyComposeTestRule
) : IvyAmountInput(composeTestRule) {
    fun selectAccount(account: String): TransactionAmountInput {
        composeTestRule.onNode(
            hasTestTag("amount_modal_account")
                .and(hasText(account))
        ).performClick()
        return this
    }
}