package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.io.network.data.LoanDTO

data class LoansResponse(
    val loans: List<LoanDTO>
)