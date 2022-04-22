package com.ivy.wallet.domain.pure.core

import arrow.core.NonEmptyList
import com.ivy.fp.Pure
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal

@Pure
fun <Arg> calcValues(
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<Arg>>,
    arg: Arg
): NonEmptyList<BigDecimal> {
    return calculateValueFunctionsSum(
        valueFunctionArgument = arg,
        transactions = transactions,
        valueFunctions = valueFunctions
    )
}