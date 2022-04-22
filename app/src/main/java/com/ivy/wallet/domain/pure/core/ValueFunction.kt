package com.ivy.wallet.domain.pure.core

import arrow.core.NonEmptyList
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal

typealias ValueFunction<A> = (Transaction, A) -> BigDecimal
typealias SuspendValueFunction<A> = suspend (Transaction, A) -> BigDecimal

internal tailrec fun <A> calculateValueFunctionsSum(
    valueFunctionArgument: A,
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<A>>,
    sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<BigDecimal> {
    return if (transactions.isEmpty())
        sum
    else
        calculateValueFunctionsSum(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNel { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
}

internal tailrec suspend fun <A> calculateValueFunctionsSumSuspend(
    valueFunctionArgument: A,
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<SuspendValueFunction<A>>,
    sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<BigDecimal> {
    return if (transactions.isEmpty())
        sum
    else
        calculateValueFunctionsSumSuspend(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNelSuspend { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
}