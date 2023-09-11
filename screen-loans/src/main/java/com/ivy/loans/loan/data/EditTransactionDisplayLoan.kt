package com.ivy.loans.loan.data

data class EditTransactionDisplayLoan(
    val isLoan: Boolean = false,
    val isLoanRecord: Boolean = false,
    val loanCaption: String? = null,
    val loanWarningDescription: String = ""
)
