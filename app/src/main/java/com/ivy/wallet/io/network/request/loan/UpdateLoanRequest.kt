package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.model.entity.Loan

data class UpdateLoanRequest(
    val loan: Loan? = null
)