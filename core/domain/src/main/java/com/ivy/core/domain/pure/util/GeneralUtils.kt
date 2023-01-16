package com.ivy.core.domain.pure.util

import arrow.core.NonEmptyList
import com.ivy.common.toNonEmptyList
import com.ivy.core.domain.action.calculate.Stats
import com.ivy.data.Value
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

val coroutineScope = CoroutineScope(Dispatchers.Default)

suspend fun <A, B> Iterable<A>.parallelMap(
    scope: CoroutineScope = coroutineScope,
    f: suspend (A) -> B
): List<B> =
    with(scope) {
        map { async { f(it) } }.awaitAll()
    }


suspend fun <A, B> Iterable<A>.parallelMapIndexed(
    scope: CoroutineScope = coroutineScope,
    f: suspend (index: Int, A) -> B
): List<B> =
    with(scope) {
        mapIndexed { index, it -> async { f(index, it) } }.awaitAll()
    }

fun dummyValue() = Value(0.0, "")

fun dummyStats() = Stats(dummyValue(), dummyValue(), dummyValue(), 0, 0)


suspend fun <Arg> sumTransactionsNew(
    transactions: List<Transaction>,
    selectors: NonEmptyList<suspend (Transaction, Arg) -> Double>,
    arg: Arg
): NonEmptyList<Double> {
    val allSums = nonEmptyListOfZeros(n = selectors.size)

    return allSums.parallelMapIndexed { index, d ->
        val valueFunction = selectors[index]

        transactions.sumOf { trn ->
            valueFunction(trn, arg)
        }
    }.toNonEmptyList()
}