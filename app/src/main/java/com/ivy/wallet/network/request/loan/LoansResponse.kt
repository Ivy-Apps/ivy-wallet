package com.ivy.wallet.network.request.loan

import com.ivy.wallet.model.entity.Loan

data class LoansResponse(
    val loans: List<Loan>
)