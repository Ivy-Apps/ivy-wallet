package com.ivy.wallet.io.network.request.transaction

import com.ivy.wallet.model.entity.Transaction

data class UpdateTransactionRequest(
    val transaction: Transaction? = null
)