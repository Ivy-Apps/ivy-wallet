package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.fp.Pure
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import java.time.LocalDateTime

@Pure
fun expenses(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.EXPENSE }
}

@Pure
fun incomes(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.INCOME }
}

@Pure
fun transfers(transactions: List<Transaction>): List<Transaction> {
    return transactions.filter { it.type == TransactionType.TRANSFER }
}

@Pure
fun isUpcoming(transaction: Transaction, timeNowUTC: LocalDateTime): Boolean =
    timeNowUTC.isBefore(transaction.dueDate)

@Pure
fun isOverdue(transaction: Transaction, timeNowUTC: LocalDateTime): Boolean =
    timeNowUTC.isAfter(transaction.dueDate)

@Pure
fun trnCurrency(
    transaction: Transaction,
    accounts: List<Account>
): Option<String> = accounts.find { it.id == transaction.accountId }?.currency.toOption()

@Pure
fun trnCurrency(
    transaction: Transaction,
    accounts: List<Account>,
    baseCurrency: String
): Option<String> =
    ((accounts.find { it.id == transaction.accountId }?.currency) ?: baseCurrency).toOption()