@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.loans.loan.LoanScreenUiTest
import com.ivy.loans.loandetails.LoanDetailScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewLoansScreenLight() {
    LoanScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewLoansScreenDark() {
    LoanScreenUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewLoanDetailsScreenLight() {
    LoanDetailScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewLoanDetailsScreenDark() {
    LoanDetailScreenUiTest(isDark = true)
}