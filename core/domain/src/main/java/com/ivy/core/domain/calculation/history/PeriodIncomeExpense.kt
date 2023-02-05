package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.calculation.RawStats
import com.ivy.core.data.common.Value

fun historyRawStats(
    transactions: List<Transaction>
): RawStats = TODO()

data class PeriodIncomeExpense(
    val income: Value,
    val expense: Value
)

context(ExchangeRates)
fun exchangeHistoryRawStats(
    historyStats: RawStats
): PeriodIncomeExpense = TODO()