package com.ivy.core.domain.pure.transaction

import arrow.core.NonEmptyList
import com.ivy.core.domain.pure.util.mapIndexedNel
import com.ivy.core.domain.pure.util.nonEmptyListOfZeros
import com.ivy.data.transaction.Transaction


/**
 * Efficiently calculates a sum of transactions given [selectors].
 *
 * @param transactions list of transactions to sum.
 * @param selectors a list of selector functions
 * transforming a transaction and [Arg] into [Double].
 * **Tip:** Use @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER") for
 * selector functions that doesn't use the [Arg] or aren't suspend.
 * @param arg argument to the passed to [selectors].
 * @return a list of sums corresponding to each [selectors] resulting sum.
 *
 *
 * ```
 *
 *
 * // Example:
 *
 * @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 * suspend fun income(trn: Transaction, arg: Unit) = when(trn.type) {
 *  TrnType.Income -> trn.amount.value
 *  else -> 0.0
 * }
 *
 * @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 * suspend fun expense(trn: Transaction, arg: Unit) = when(trn.type) {
 *  TrnType.Expense -> trn.amount.value
 *  else -> 0.0
 * }
 *
 * val res = sumTransactions(
 *  transactions = trns,
 *  selectors = nonEmptyListOf(
 *   ::income,
 *   ::expense
 *  ),
 *  arg = Unit
 * )
 * println("Income = $res[0]")
 * println("Expense = $res[1]")
 * ```
 */
@Deprecated("inefficient - will be replaced with `account-cache` algo")
suspend fun <Arg> sumTransactions(
    transactions: List<Transaction>,
    selectors: NonEmptyList<suspend (Transaction, Arg) -> Double>,
    arg: Arg
): NonEmptyList<Double> {
    var allSums = nonEmptyListOfZeros(n = selectors.size)

    for (trn in transactions) {
        allSums = allSums.mapIndexedNel { index, sum ->
            val valueFunction = selectors[index]
            sum + valueFunction(trn, arg)
        }
    }

    return allSums
}