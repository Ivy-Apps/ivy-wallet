package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.model.LoanType

class LoansScreen<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    fun addLoan() {
        composeTestRule.onNodeWithText("Add loan")
            .performClick()
    }

    fun assertLoan(
        name: String,
        amount: String,
        loanType: LoanType,
        amountDecimal: String,
        amountPaid: String,
        percentPaid: String,
        currency: String = "USD"
    ) {
        val typeText = when (loanType) {
            LoanType.BORROW -> "BORROWED"
            LoanType.LEND -> "LENT"
        }

        composeTestRule.onNode(hasTestTag("loan_item"))
            .assertTextEquals(
                name, typeText, amount, amountDecimal, currency,
                "$amountPaid $currency / $amount$amountDecimal $currency ($percentPaid%)"
            )
    }

    fun clickLoan(loanName: String) {
        composeTestRule.onNode(
            hasTestTag("loan_item")
                .and(hasText(loanName, substring = true))
        ).performClick()
    }
}