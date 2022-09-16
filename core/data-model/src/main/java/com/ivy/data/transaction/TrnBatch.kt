package com.ivy.data.transaction

data class TrnBatch(
    val batchId: String,
    val trns: List<Transaction>
)