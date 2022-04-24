package com.ivy.wallet.ui.loan.data

import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.LoanRecord

data class DisplayLoanRecord(
    val loanRecord: LoanRecord,
    val account: Account? = null,
    val loanRecordCurrencyCode: String = "",
    val loanCurrencyCode: String = "",
    val loanRecordTransaction: Boolean = false,
)
