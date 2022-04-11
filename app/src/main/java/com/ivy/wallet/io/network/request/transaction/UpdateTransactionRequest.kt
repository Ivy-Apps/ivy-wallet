package com.ivy.wallet.io.network.request.transaction

import com.ivy.wallet.domain.data.entity.Transaction

data class UpdateTransactionRequest(
    val transaction: Transaction? = null
)