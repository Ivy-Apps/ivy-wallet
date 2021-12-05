package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.ui.theme.Ivy

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

        composeTestRule.onNode(
            hasTestTag("loan_item")
                .and(
                    hasText(name, substring = true)
                ),
        )
            .performScrollTo()
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

    fun addLoanFlow(
        loanName: String,
        amount: String,
        color: Color = Ivy,
        icon: String = "account",
        loanType: LoanType
    ) {
        clickAddLoan()
        loanModal.apply {
            enterName(loanName)
            enterAmount(amount)
            selectLoanType(loanType)
            colorPicker.chooseColor(color)
            chooseIconFlow.chooseIcon(icon)

            clickAdd()
        }

        assertLoan(
            name = loanName,
            amount = amount.split(".").first(),
            amountDecimal = amount.split(".").getOrNull(1)?.let { ".$it" } ?: ".00",
            loanType = loanType,
            currency = "USD",
            amountPaid = "0.00",
            percentPaid = "0.00"
        )
    }
}