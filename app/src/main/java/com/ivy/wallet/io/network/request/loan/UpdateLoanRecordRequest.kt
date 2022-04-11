package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.domain.data.entity.LoanRecord

data class UpdateLoanRecordRequest(
    val loanRecord: LoanRecord? = null
)