package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.printTree

class LoanDetailsScreen(
    private val composeTestRule: IvyComposeTestRule
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

    fun addRecord() {
        composeTestRule.onNodeWithText("Add record")
            .performClick()
    }

    fun clickLoanRecord(
        amount: String,
        note: String? = null,
    ) {
        var matcher = hasTestTag("loan_record_item")
            .and(hasAnyDescendant(hasText(amount)))

        if (note != null) {
            matcher = matcher.and(hasAnyDescendant(hasText(note)))
        }

        composeTestRule.printTree()

        composeTestRule.onNode(matcher, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performScrollTo()
            .performClick()
    }

    fun assertNoRecordsEmptyState() {
        composeTestRule.onNodeWithText("No records")
            .assertIsDisplayed()
    }
}