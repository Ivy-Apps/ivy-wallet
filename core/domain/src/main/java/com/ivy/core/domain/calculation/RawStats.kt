package com.ivy.core.domain.calculation

import com.ivy.core.data.calculation.RawStats
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.NonNegativeInt
import com.ivy.core.data.common.PositiveDouble
import com.ivy.core.data.common.toNonNegativeUnsafe
import com.ivy.core.data.optimized.LedgerEntry
import java.time.LocalDateTime

/**
 * We use Imperative style because this operation is performance-critical for the app.
 */
fun rawStats(
    entries: List<LedgerEntry>,
    interpretTransfer: (LedgerEntry.Transfer) -> List<LedgerEntry.Single>
): RawStats {
    val incomes = mutableMapOf<AssetCode, Double>()
    val expenses = mutableMapOf<AssetCode, Double>()
    var incomesCount = 0
    var expensesCount = 0
    var newestTrnTime = LocalDateTime.MIN

    fun updateNewestTime(entry: LedgerEntry) {
        if (entry.time > newestTrnTime) {
            newestTrnTime = entry.time
        }
    }

    fun processIncome(income: LedgerEntry.Single.Income) {
        incomesCount++
        incomes.aggregate(income.value.asset, income.value.amount)
        updateNewestTime(income)
    }

    fun processExpense(expense: LedgerEntry.Single.Expense) {
        expensesCount++
        expenses.aggregate(expense.value.asset, expense.value.amount)
        updateNewestTime(expense)
    }

    entries.forEach { entry ->
        when (entry) {
            is LedgerEntry.Single.Expense -> processExpense(entry)
            is LedgerEntry.Single.Income -> processIncome(entry)
            is LedgerEntry.Transfer -> interpretTransfer(entry).forEach {
                when (it) {
                    is LedgerEntry.Single.Expense -> processExpense(it)
                    is LedgerEntry.Single.Income -> processIncome(it)
                }
            }
        }
    }

    return RawStats(
        incomes = incomes.mapValues { PositiveDouble.fromDoubleUnsafe(it.value) },
        expenses = expenses.mapValues { PositiveDouble.fromDoubleUnsafe(it.value) },
        incomesCount = incomesCount.toNonNegativeUnsafe(),
        expensesCount = expensesCount.toNonNegativeUnsafe(),
        newestTransaction = newestTrnTime,
    )
}

/**
 * Sums all values in two [RawStats] instances.
 *
 * @return RawStats picking the largest newestTrnTime
 *
 * Complexity:
 * **O(m+n) space-time**
 * where:
 * - m = Left's RawStats incomes & expenses maps size
 * - n = Right's RawStats incomes & expenses maps size
 */
infix operator fun RawStats.plus(other: RawStats): RawStats {
    fun sumMaps(
        map1: Map<AssetCode, PositiveDouble>,
        other: Map<AssetCode, PositiveDouble>,
    ): Map<AssetCode, PositiveDouble> {
        val sum = mutableMapOf<AssetCode, Double>()
        map1.forEach(sum::aggregate)
        other.forEach(sum::aggregate)
        return sum.mapValues { PositiveDouble.fromDoubleUnsafe(it.value) }
    }

    return RawStats(
        incomes = sumMaps(incomes, other.incomes),
        expenses = sumMaps(expenses, other.expenses),
        incomesCount = NonNegativeInt.fromIntUnsafe(incomesCount.value + other.incomesCount.value),
        expensesCount = NonNegativeInt.fromIntUnsafe(expensesCount.value + other.expensesCount.value),
        newestTransaction = maxOf(newestTransaction, other.newestTransaction)
    )
}

private fun MutableMap<AssetCode, Double>.aggregate(
    key: AssetCode,
    amount: PositiveDouble,
) {
    compute(key) { _, currentAmount ->
        (currentAmount ?: 0.0) + amount.value
    }
}
