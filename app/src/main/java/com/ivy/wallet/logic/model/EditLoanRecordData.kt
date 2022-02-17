package com.ivy.wallet.logic.model

import com.ivy.wallet.model.entity.LoanRecord

data class EditLoanRecordData(
    val loanRecord: LoanRecord,
    val createLoanRecordTransaction: Boolean = false,
    val reCalculateLoanAmount: Boolean = false
)