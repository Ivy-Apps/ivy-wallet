package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.data.Stats
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import com.ivy.data.exchange.ExchangeRates

suspend fun exchangeRawStats(
    rawStats: RawStats,
    rates: ExchangeRates,
    outputCurrency: CurrencyCode
): Stats {
    var income = 0.0
    var expense = 0.0

    rawStats.incomes.forEach { (currency, amount) ->
        income += rates.exchange(
            from = currency,
            to = outputCurrency,
            amount = amount
        )
    }

    rawStats.expenses.forEach { (currency, amount) ->
        expense += rates.exchange(
            from = currency,
            to = outputCurrency,
            amount = amount
        )
    }

    return Stats(
        income = Value(income, outputCurrency),
        expense = Value(expense, outputCurrency),
        incomesCount = rawStats.incomesCount,
        expensesCount = rawStats.expensesCount
    )
}