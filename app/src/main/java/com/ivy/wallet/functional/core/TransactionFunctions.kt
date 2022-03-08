package com.ivy.wallet.functional.core

import com.ivy.wallet.functional.data.FPTransaction
import com.ivy.wallet.functional.data.toFPTransaction
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Transaction
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