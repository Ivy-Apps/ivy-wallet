package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.frp.Pure
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.account.accountCurrency
import java.time.LocalDate

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
fun isUpcoming(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.dueDate?.toLocalDate() ?: return false
    return dateNow.isBefore(dueDate) || dateNow.isEqual(dueDate)
}

@Pure
fun isOverdue(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.dueDate?.toLocalDate() ?: return false
    return dateNow.isAfter(dueDate)
}

@Pure
fun trnCurrency(
    transaction: Transaction,
    accounts: List<Account>,
    baseCurrency: String
): Option<String> {
    val account = accounts.find { it.id == transaction.accountId }
        ?: return baseCurrency.toOption()
    return accountCurrency(account, baseCurrency).toOption()
}