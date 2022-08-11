package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.frp.Pure
import com.ivy.wallet.domain.pure.account.accountCurrency
import java.time.LocalDate

@Pure
fun expenses(transactions: List<TransactionOld>): List<TransactionOld> {
    return transactions.filter { it.type == TrnType.EXPENSE }
}

@Pure
fun incomes(transactions: List<TransactionOld>): List<TransactionOld> {
    return transactions.filter { it.type == TrnType.INCOME }
}

@Pure
fun transfers(transactions: List<TransactionOld>): List<TransactionOld> {
    return transactions.filter { it.type == TrnType.TRANSFER }
}

@Pure
fun isUpcoming(transaction: TransactionOld, dateNow: LocalDate): Boolean {
    val dueDate = transaction.dueDate?.toLocalDate() ?: return false
    return dateNow.isBefore(dueDate) || dateNow.isEqual(dueDate)
}

@Pure
fun isOverdue(transaction: TransactionOld, dateNow: LocalDate): Boolean {
    val dueDate = transaction.dueDate?.toLocalDate() ?: return false
    return dateNow.isAfter(dueDate)
}

@Pure
fun trnCurrency(
    transaction: TransactionOld,
    accounts: List<AccountOld>,
    baseCurrency: String
): Option<String> {
    val account = accounts.find { it.id == transaction.accountId }
        ?: return baseCurrency.toOption()
    return accountCurrency(account, baseCurrency).toOption()
}