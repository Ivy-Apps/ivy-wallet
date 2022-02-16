package com.ivy.wallet.ui.loan.data

import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.LoanRecord

data class DisplayLoanRecord(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
