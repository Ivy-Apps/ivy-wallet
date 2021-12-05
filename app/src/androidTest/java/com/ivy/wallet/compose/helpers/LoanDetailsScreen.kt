package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule

class LoanDetailsScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun clickEdit() {
        composeTestRule.onNodeWithText("Edit")
            .performClick()
    }

    fun assertLoanAmount(
        amount: String,
        amountDecimal: String,
        currency: String = "USD"
    ) {
        composeTestRule.onNodeWithTag("loan_amount")
            .assertTextEquals(currency, amount, amountDecimal)
    }

    fun assertLoanName(
        loanName: String
    ) {
        composeTestRule.onNodeWithTag("loan_name", useUnmergedTree = true)
            .assertTextEquals(loanName)
    }

    fun assertAmountPaid(
        amountPaid: String,
        loanAmount: String
    ) {
        composeTestRule.onNodeWithTag("amount_paid", useUnmergedTree = true)
            .assertTextEquals("$amountPaid / $loanAmount")
    }

    fun assertPercentPaid(
        percentPaid: String
    ) {
        composeTestRule.onNodeWithTag("percent_paid", useUnmergedTree = true)
            .assertTextEquals(percentPaid)
    }

    fun assertLeftToPay(
        leftToPayAmount: String,
        currency: String = "USD"
    ) {
        composeTestRule.onNodeWithTag("left_to_pay", useUnmergedTree = true)
            .assertTextEquals("$leftToPayAmount $currency left")
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
    }

    fun clickClose() {
        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()
    }
}