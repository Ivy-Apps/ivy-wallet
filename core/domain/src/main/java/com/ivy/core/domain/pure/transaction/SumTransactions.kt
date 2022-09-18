package com.ivy.core.domain.pure.transaction

import arrow.core.NonEmptyList
import com.ivy.core.domain.pure.util.mapIndexedNel
import com.ivy.core.domain.pure.util.nonEmptyListOfZeros
import com.ivy.data.transaction.Transaction
import com.ivy.frp.Pure

/**
 * Calculates a list sums of transactions given a list of selectors.
 *
 * **Tip:** Use @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER") for
 * selector functions that doesn't [Arg] or aren't suspend.
 */
@Pure
suspend fun <Arg> sumTransactions(
    transactions: List<Transaction>,
    selectorFunctions: NonEmptyList<suspend (Transaction, Arg) -> Double>,
    arg: Arg
): NonEmptyList<Double> {
    var allSums = nonEmptyListOfZeros(n = selectorFunctions.size)

    for (trn in transactions) {
        allSums = allSums.mapIndexedNel { index, sum ->
            val valueFunction = selectorFunctions[index]
            sum + valueFunction(trn, arg)
        }
    }

    return allSums
}