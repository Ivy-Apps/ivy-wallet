package com.ivy.core.domain.pure.transaction

import arrow.core.NonEmptyList
import com.ivy.core.domain.pure.util.mapIndexedNel
import com.ivy.core.domain.pure.util.nonEmptyListOfZeros
import com.ivy.data.transaction.Transaction
import com.ivy.frp.Pure


/**
 * Helpful annotations:
 *
 * ## @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 */
typealias SelectorFunction<A> = suspend (Transaction, A) -> Double

/**
 * ## @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 */
@Pure
suspend fun <Arg> sumTransactions(
    transactions: List<Transaction>,
    selectorFunctions: NonEmptyList<SelectorFunction<Arg>>,
    arg: Arg
): NonEmptyList<Double> = sumTransactionsInternal(
    valueFunctionArgument = arg,
    transactions = transactions,
    selectorFunctions = selectorFunctions
)

@Pure
private tailrec suspend fun <A> sumTransactionsInternal(
    transactions: List<Transaction>,
    valueFunctionArgument: A,
    selectorFunctions: NonEmptyList<SelectorFunction<A>>,
    sum: NonEmptyList<Double> = nonEmptyListOfZeros(n = selectorFunctions.size)
): NonEmptyList<Double> {
    return if (transactions.isEmpty())
        sum
    else
        sumTransactionsInternal(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            selectorFunctions = selectorFunctions,
            sum = sum.mapIndexedNel { index, sumValue ->
                val valueFunction = selectorFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
}