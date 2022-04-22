package com.ivy.wallet.domain.pure.core

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal

suspend fun <A> sum(
    transactions: List<Transaction>,
    valueFunction: SuspendValueFunction<A>,
    argument: A
): BigDecimal {
    return transactions.sumOf {
        valueFunction(it, argument)
    }
}

fun expenses(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.EXPENSE }
}

fun incomes(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.INCOME }
}

fun transfers(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.TRANSFER }
}