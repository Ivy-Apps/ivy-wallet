package com.ivy.core.domain.action.calculate

import com.ivy.data.transaction.Value

data class Stats(
    val balance: Value,
    val income: Value,
    val expense: Value,
    val incomesCount: Int,
    val expensesCount: Int,
)