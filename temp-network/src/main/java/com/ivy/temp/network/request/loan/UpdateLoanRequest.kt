package com.ivy.wallet.io.network.request.loan

import com.ivy.wallet.io.network.data.LoanDTO

data class UpdateLoanRequest(
    val loan: LoanDTO? = null
)