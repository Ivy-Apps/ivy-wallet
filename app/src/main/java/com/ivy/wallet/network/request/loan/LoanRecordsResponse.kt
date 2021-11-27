package com.ivy.wallet.network.request.loan

import com.ivy.wallet.model.entity.LoanRecord

data class LoanRecordsResponse(
    val loanRecords: List<LoanRecord>
)