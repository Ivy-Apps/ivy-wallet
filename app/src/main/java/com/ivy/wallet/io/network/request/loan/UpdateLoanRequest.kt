package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.domain.data.entity.Loan

data class UpdateLoanRequest(
    val loan: Loan? = null
)