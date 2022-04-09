package com.ivy.wallet.io.network.request.transaction

import com.ivy.wallet.domain.data.entity.Transaction


data class TransactionsResponse(
    val transactions: List<Transaction>
)