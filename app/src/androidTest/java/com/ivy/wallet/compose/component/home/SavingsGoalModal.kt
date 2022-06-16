package com.ivy.wallet.compose.component.home

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.amountinput.AmountInput
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput

class SavingsGoalModal(
    private val composeTestRule: IvyComposeTestRule
) : AmountInput<SavingsGoalModal> {
    private val amountInput = IvyAmountInput(composeTestRule)

    override fun enterAmount(number: String): SavingsGoalModal {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()
        amountInput.enterNumber(number, next = this)
        return this
    }


    fun clickSave(): HomeMoreMenu {
        composeTestRule.onNodeWithText("Save")
            .performClick()
        return HomeMoreMenu(composeTestRule)
    }
}