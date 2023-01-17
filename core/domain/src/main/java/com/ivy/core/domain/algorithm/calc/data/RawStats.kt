package com.ivy.core.domain.algorithm.calc.data

import com.ivy.data.CurrencyCode

data class RawStats(
    val incomes: Map<CurrencyCode, Double>,
    val expenses: Map<CurrencyCode, Double>,
    val incomesCount: Int,
    val expensesCount: Int,
)