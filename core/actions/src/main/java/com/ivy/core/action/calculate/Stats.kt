package com.ivy.core.action.calculate

import com.ivy.data.transaction.Transaction

data class Stats(
    val balance: Double,
    val income: Double,
    val expense: Double,
    val incomesCount: Int,
    val expensesCount: Int,
    val trns: List<Transaction>
)