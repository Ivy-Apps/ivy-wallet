package com.ivy.data.transaction

data class Transfer(
    val batchId: String,
    val time: TrnTime,
    val from: Transaction,
    val to: Transaction,
    val fee: Transaction?
)