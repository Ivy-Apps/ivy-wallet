package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.common.Value
import com.ivy.core.domain.data.RawStats

fun periodRawStats(
    transactions: List<Transaction>
): RawStats = TODO()

data class PeriodIncomeExpense(
    val income: Value,
    val expense: Value
)

context(ExchangeRates)
fun exchangePeriodRawStats(
    periodStats: RawStats
): PeriodIncomeExpense = TODO()