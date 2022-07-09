package com.ivy.base

data class EditTransactionDisplayLoan(
    val isLoan: Boolean = false,
    val isLoanRecord: Boolean = false,
    val loanCaption: String? = null,
    val loanWarningDescription: String = ""
)
