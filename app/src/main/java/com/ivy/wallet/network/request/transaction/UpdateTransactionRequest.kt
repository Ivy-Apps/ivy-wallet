package com.ivy.wallet.network.request.transaction

import com.ivy.wallet.model.entity.Transaction

data class UpdateTransactionRequest(
    val transaction: Transaction? = null
)