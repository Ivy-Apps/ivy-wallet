package com.ivy.wallet.domain.fp.core

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.FPTransaction
import com.ivy.wallet.domain.fp.data.toFPTransaction
import java.math.BigDecimal

suspend fun <A> sum(
    transactions: List<FPTransaction>,
    valueFunction: SuspendValueFunction<A>,
    argument: A
): BigDecimal {
    return transactions.sumOf {
        valueFunction(it, argument)
    }
}

fun expenses(transactions: List<FPTransaction>): List<FPTransaction> {
    return transactions.filter { it.type == TransactionType.EXPENSE }
}

fun incomes(transactions: List<FPTransaction>): List<FPTransaction> {
    return transactions.filter { it.type == TransactionType.INCOME }
}

fun transfers(transactions: List<FPTransaction>): List<FPTransaction> {
    return transactions.filter { it.type == TransactionType.TRANSFER }
}

fun List<Transaction>.toFPTransactions(): List<FPTransaction> {
    return this.map { it.toFPTransaction() }
}