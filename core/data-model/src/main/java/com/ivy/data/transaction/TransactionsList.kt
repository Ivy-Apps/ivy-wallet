package com.ivy.data.transaction

data class TransactionsList(
    val upcoming: DueSection?,
    val overdue: DueSection?,
    val history: List<TrnListItem>,
)
