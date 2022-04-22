package com.ivy.wallet.domain.pure.core

import arrow.core.NonEmptyList
import com.ivy.fp.Pure
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.util.mapIndexedNel
import com.ivy.wallet.domain.pure.util.mapIndexedNelSuspend
import com.ivy.wallet.domain.pure.util.nonEmptyListOfZeros
import java.math.BigDecimal

@Pure
fun <Arg> calcValues(
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<Arg>>,
    arg: Arg
): NonEmptyList<BigDecimal> = calculateValueFunctionsSum(
    valueFunctionArgument = arg,
    transactions = transactions,
    valueFunctions = valueFunctions
)


typealias ValueFunction<A> = (Transaction, A) -> BigDecimal
typealias SuspendValueFunction<A> = suspend (Transaction, A) -> BigDecimal

@Pure
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

@Pure
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