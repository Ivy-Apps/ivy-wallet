package com.ivy.wallet.compose.component.loan

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DeleteConfirmationModal
import com.ivy.wallet.compose.component.DeleteItem
import com.ivy.wallet.compose.util.printTree

class LoanDetailsScreen(
    private val composeTestRule: IvyComposeTestRule
) : DeleteItem<LoansScreen> {

    fun clickEdit(): LoanModal {
        composeTestRule.onNodeWithText("Edit")
            .performClick()
        return LoanModal(composeTestRule)
    }

    fun assertLoanAmount(
        amount: String,
        amountDecimal: String,
        currency: String = "USD"
    ): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("loan_amount")
            .assertTextEquals(currency, amount, amountDecimal)
        return this
    }

    fun assertLoanName(
        loanName: String
    ): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("loan_name", useUnmergedTree = true)
            .assertTextEquals(loanName)
        return this
    }

    fun assertAmountPaid(
        amountPaid: String,
        loanAmount: String
    ): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("amount_paid", useUnmergedTree = true)
            .assertTextEquals("$amountPaid / $loanAmount")
        return this
    }

    fun assertPercentPaid(
        percentPaid: String
    ): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("percent_paid", useUnmergedTree = true)
            .assertTextEquals(percentPaid)
        return this
    }

    fun assertLeftToPay(
        leftToPayAmount: String,
        currency: String = "USD"
    ): LoanDetailsScreen {
        composeTestRule.onNodeWithTag("left_to_pay", useUnmergedTree = true)
            .assertTextEquals("$leftToPayAmount $currency left")
        return this
    }

    private fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    fun clickClose(): LoansScreen {
        composeTestRule.onNodeWithTag("toolbar_close")
            .performClick()
        return LoansScreen(composeTestRule)
    }

    fun addRecord(): LoanRecordModal {
        composeTestRule.onNodeWithText("Add record")
            .performClick()
        return LoanRecordModal(composeTestRule)
    }

    fun clickLoanRecord(
        amount: String,
        note: String? = null,
    ): LoanRecordModal {
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

        return LoanRecordModal(composeTestRule)
    }

    fun assertNoRecordsEmptyState(): LoanDetailsScreen {
        composeTestRule.onNodeWithText("No records")
            .assertIsDisplayed()
        return this
    }

    override fun deleteWithConfirmation(): LoansScreen {
        return clickDelete()
            .confirmDelete(next = LoansScreen(composeTestRule))
    }
}