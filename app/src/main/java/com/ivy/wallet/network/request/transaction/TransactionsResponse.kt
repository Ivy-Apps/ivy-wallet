package com.ivy.wallet.network.request.transaction

import com.ivy.wallet.model.entity.Transaction


data class TransactionsResponse(
    val transactions: List<Transaction>
)