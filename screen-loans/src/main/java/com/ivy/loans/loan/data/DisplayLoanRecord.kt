package com.ivy.loans.loan.data

import com.ivy.domain.datamodel.Account
import com.ivy.domain.datamodel.LoanRecord

data class DisplayLoanRecord(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
