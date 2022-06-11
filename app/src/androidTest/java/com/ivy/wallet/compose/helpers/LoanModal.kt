package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.domain.data.LoanType

class LoanModal(
    private val composeTestRule: IvyComposeTestRule
) {
    private val amountInput = IvyAmountInput(composeTestRule)
    val colorPicker = IvyColorPicker(composeTestRule)
    val chooseIconFlow = ChooseIconFlow(composeTestRule)
    val accountsTab = AccountsTab(composeTestRule)

    fun enterName(loanName: String) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(loanName)
    }

    fun selectLoanType(loanType: LoanType) {
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
    }

    fun enterAmount(amount: String) {
        composeTestRule.onNodeWithTag("amount_balance")
            .performClick()

        amountInput.enterNumber(amount)
    }

    fun clickAdd() {
        composeTestRule.onNodeWithText("Add")
            .performClick()
    }

    fun clickSave() {
        composeTestRule.onNodeWithText("Save")
            .performClick()
    }

    fun selectDefaultLoanAccount(){
        accountsTab.clickAccount(account = "Cash")
    }
}