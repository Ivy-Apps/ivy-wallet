package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.model.LoanType

class LoansScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    private val loanModal = LoanModal(composeTestRule)

    fun clickAddLoan() {
        composeTestRule.onNodeWithText("Add loan")
            .performClick()
    }

    fun assertLoan(
        name: String,
        loanType: LoanType,
        amountLeft: String,
        amountLeftDecimal: String,
        loanAmount: String,
        amountPaid: String,
        percentPaid: String,
        currency: String = "USD"
    ) {
        val typeText = when (loanType) {
            LoanType.BORROW -> "BORROWED"
            LoanType.LEND -> "LENT"
        }

        composeTestRule.onNode(
            hasTestTag("loan_item")
                .and(
                    hasText(name, substring = true)
                ),
        )
            .performScrollTo()
            .assertTextEquals(
                name, typeText, amountLeft, amountLeftDecimal, currency,
                "$amountPaid $currency / $loanAmount $currency ($percentPaid%)"
            )
    }

    fun clickLoan(loanName: String) {
        composeTestRule.onNode(
            hasTestTag("loan_item")
                .and(hasText(loanName, substring = true))
        ).performClick()
    }

    fun addLoanFlow(
        loanName: String,
        amount: String,
        color: Color? = null,
        icon: String? = null,
        loanType: LoanType
    ) {
        clickAddLoan()
        loanModal.apply {
            enterName(loanName)
            enterAmount(amount)
            selectLoanType(loanType)
            if (color != null) {
                colorPicker.chooseColor(color)
            }
            if (icon != null) {
                chooseIconFlow.chooseIcon(icon)
            }

            clickAdd()
        }

        val loanAmount = amount.split(".").first()
        val loanAmountDecimal = amount.split(".").getOrNull(1)?.let { ".$it" } ?: ".00"
        assertLoan(
            name = loanName,
            loanType = loanType,
            amountLeft = loanAmount,
            amountLeftDecimal = loanAmountDecimal,
            loanAmount = loanAmount + loanAmountDecimal,
            currency = "USD",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
    }

    fun assertEmptyState() {
        composeTestRule.onNodeWithText("No loans")
            .assertIsDisplayed()
    }
}