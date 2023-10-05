package com.ivy.loans.loandetails.events

sealed class DeleteLoanModalState: LoanDetailsScreenEvent() {
    data object OnDeleteLoan : DeleteLoanModalState()
    data class OnDismissDeleteLoan(val isDeleteModalVisible: Boolean) : DeleteLoanModalState()
}