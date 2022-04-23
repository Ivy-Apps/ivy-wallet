package com.ivy.wallet.domain.pure.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction

fun expenses(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.EXPENSE }
}

fun incomes(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.INCOME }
}

fun transfers(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.TRANSFER }
}