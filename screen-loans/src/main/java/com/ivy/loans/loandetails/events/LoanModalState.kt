package com.ivy.loans.loandetails.events

import com.ivy.legacy.datamodel.Loan

sealed class LoanModalState: LoanDetailsScreenEvent() {
    data object OnDismissLoanModal : LoanModalState()
    data class OnEditLoanModal(val loan: Loan, val createLoanTransaction: Boolean) : LoanModalState()
    data object PerformCalculation : LoanModalState()
}