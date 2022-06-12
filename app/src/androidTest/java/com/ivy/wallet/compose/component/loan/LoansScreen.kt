package com.ivy.wallet.compose.component.loan

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.domain.data.LoanType

class LoansScreen(
    private val composeTestRule: IvyComposeTestRule
) {

    fun clickAddLoan(): LoanModal {
        composeTestRule.onNodeWithText("Add loan")
            .performClick()
        return LoanModal(composeTestRule)
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
    ): LoansScreen {
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
        return this
    }

    fun clickLoan(loanName: String): LoanDetailsScreen {
        composeTestRule.onNode(
            hasTestTag("loan_item")
                .and(hasText(loanName, substring = true))
        ).performClick()
        return LoanDetailsScreen(composeTestRule)
    }

    fun addLoanFlow(
        loanName: String,
        amount: String,
        color: Color? = null,
        icon: String? = null,
        loanType: LoanType
    ): LoansScreen {
        clickAddLoan()
            .enterName(loanName)
            .enterAmount(amount)
            .selectLoanType(loanType)
            .selectDefaultLoanAccount()
            .apply {
                if (color != null) {
                    chooseColor(color)
                }
            }
            .apply {
                if (icon != null) {
                    chooseIcon(icon)
                }
            }
            .clickAdd()

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

        return this
    }

    fun assertEmptyState() {
        composeTestRule.onNodeWithText("No loans")
            .assertIsDisplayed()
    }
}