package com.ivy.core.action.calculate

data class Stats(
    val balance: Double,
    val income: Double,
    val expense: Double,
    val incomesCount: Int,
    val expensesCount: Int
)