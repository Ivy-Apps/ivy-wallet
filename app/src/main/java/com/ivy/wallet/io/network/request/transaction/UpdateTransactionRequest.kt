package com.ivy.wallet.io.network.request.transaction

import com.ivy.wallet.io.network.data.TransactionDTO

data class UpdateTransactionRequest(
    val transaction: TransactionDTO? = null
)