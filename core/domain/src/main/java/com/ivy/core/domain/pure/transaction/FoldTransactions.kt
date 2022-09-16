package com.ivy.core.domain.pure.transaction

import arrow.core.NonEmptyList
import com.ivy.core.domain.pure.mapIndexedNel
import com.ivy.core.domain.pure.nonEmptyListOfZeros
import com.ivy.data.transaction.Transaction
import com.ivy.frp.Pure


/**
 * Helpful annotations:
 *
 * ## @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 */
typealias ValueFunction<A> = suspend (Transaction, A) -> Double

/**
 * ## @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
 */
@Pure
suspend fun <Arg> foldTransactions(
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<Arg>>,
    arg: Arg
): NonEmptyList<Double> = sumTransactionsInternal(
    valueFunctionArgument = arg,
    transactions = transactions,
    valueFunctions = valueFunctions
)

@Pure
internal tailrec suspend fun <A> sumTransactionsInternal(
    transactions: List<Transaction>,
    valueFunctionArgument: A,
    valueFunctions: NonEmptyList<ValueFunction<A>>,
    sum: NonEmptyList<Double> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<Double> {
    return if (transactions.isEmpty())
        sum
    else
        sumTransactionsInternal(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNel { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
}