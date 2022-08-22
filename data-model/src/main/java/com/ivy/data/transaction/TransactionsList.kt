package com.ivy.data.transaction

data class TransactionsList(
    val upcoming: UpcomingSection?,
    val overdue: OverdueSection?,
    val history: List<TrnListItem>,
)
