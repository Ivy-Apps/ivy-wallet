package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType

/**
 *
 */
fun rawStats(trns: List<CalcTrn>): RawStats {
    val incomes = mutableMapOf<CurrencyCode, Double>()
    val expenses = mutableMapOf<CurrencyCode, Double>()
    var incomesCount = 0
    var expensesCount = 0

    trns.forEach { trn ->
        when (trn.type) {
            TransactionType.Income -> {
                incomesCount++
                incomes.aggregate(trn)
            }
            TransactionType.Expense -> {
                expensesCount++
                expenses.aggregate(trn)
            }
        }
    }

    return RawStats(
        incomes = incomes,
        expenses = expenses,
        incomesCount = incomesCount,
        expensesCount = expensesCount,
    )
}

/**
 * Sums all values in two [RawStats] instances.
 *
 * Complexity:
 * **O(m+n) space-time**
 * where:
 * - m = Left's RawStats incomes & expenses maps size
 * - n = Right's RawStats incomes & expenses maps size
 */
infix operator fun RawStats.plus(other: RawStats): RawStats {
    fun sumMaps(
        map1: Map<CurrencyCode, Double>,
        map2: Map<CurrencyCode, Double>
    ): Map<CurrencyCode, Double> {
        val sum = mutableMapOf<CurrencyCode, Double>()
        map1.forEach(sum::aggregate)
        map2.forEach(sum::aggregate)
        return sum
    }

    return RawStats(
        incomes = sumMaps(incomes, other.incomes),
        expenses = sumMaps(expenses, other.expenses),
        incomesCount = incomesCount + other.incomesCount,
        expensesCount = expensesCount + other.expensesCount
    )
}

private fun MutableMap<CurrencyCode, Double>.aggregate(
    trn: CalcTrn
) = aggregate(currency = trn.currency, amount = trn.amount)

private fun MutableMap<CurrencyCode, Double>.aggregate(
    currency: CurrencyCode,
    amount: Double,
) {
    compute(currency) { _, oldValue ->
        (oldValue ?: 0.0) + amount
    }
}