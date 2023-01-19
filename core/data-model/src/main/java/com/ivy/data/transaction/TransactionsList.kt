package com.ivy.data.transaction

@Deprecated("don't use - use the one `algorithms`")
data class TransactionsList(
    val upcoming: DueSection?,
    val overdue: DueSection?,
    val history: List<TrnListItem>,
)
