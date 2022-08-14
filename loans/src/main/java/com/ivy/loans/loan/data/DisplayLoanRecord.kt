package com.ivy.wallet.ui.loan.data

import com.ivy.data.AccountOld
import com.ivy.data.loan.LoanRecord

data class DisplayLoanRecord(
    val loanRecord: LoanRecord,
    val account: AccountOld? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
