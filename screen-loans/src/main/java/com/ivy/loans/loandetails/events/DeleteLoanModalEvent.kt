package com.ivy.loans.loandetails.events

sealed class DeleteLoanModalEvent : LoanDetailsScreenEvent() {
    data object OnDeleteLoan : DeleteLoanModalEvent()
    data class OnDismissDeleteLoan(val isDeleteModalVisible: Boolean) : DeleteLoanModalEvent()
}
