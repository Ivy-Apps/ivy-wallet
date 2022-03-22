package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.model.LoanType

class LoanModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val amountInput = AmountInput(composeTestRule)
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