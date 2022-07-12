package com.ivy.wallet.io.network.request.transaction

import com.ivy.wallet.io.network.data.TransactionDTO


data class TransactionsResponse(
    val transactions: List<TransactionDTO>
)