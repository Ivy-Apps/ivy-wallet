package com.ivy.core.domain.calculation.history.data

import com.ivy.core.data.common.Value
import com.ivy.core.domain.api.data.period.TransactionListItem

data class PeriodData(
    val periodIncome: Value,
    val periodExpense: Value,
    val transactionList: List<TransactionListItem>
)