package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.io.network.data.LoanRecordDTO

data class LoanRecordsResponse(
    val loanRecords: List<LoanRecordDTO>
)