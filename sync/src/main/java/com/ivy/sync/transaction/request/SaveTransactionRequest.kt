package com.ivy.sync.transaction.request

import com.ivy.wallet.io.network.data.TransactionDTO

data class SaveTransactionRequest(
    val transaction: TransactionDTO?
)