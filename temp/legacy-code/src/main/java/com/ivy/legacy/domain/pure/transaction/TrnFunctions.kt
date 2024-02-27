package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.base.model.TransactionType
import com.ivy.base.time.convertToLocal
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.temp.migration.getAccountId

import com.ivy.frp.Pure
import com.ivy.legacy.datamodel.Account
import com.ivy.wallet.domain.pure.account.accountCurrency
import java.time.LocalDate

@Pure
fun expenses(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Expense>()
}

@Pure
fun incomes(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Income>()
}

@Pure
fun transfers(transactions: List<Transaction>): List<Transaction> {
    return transactions.filterIsInstance<Transfer>()
}

@Pure
fun isUpcoming(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.time.convertToLocal().toLocalDate() ?: return false
    return dateNow.isBefore(dueDate) || dateNow.isEqual(dueDate)
}

@Pure
fun isOverdue(transaction: Transaction, dateNow: LocalDate): Boolean {
    val dueDate = transaction.time.convertToLocal().toLocalDate() ?: return false
    return dateNow.isAfter(dueDate)
}

@Pure
fun trnCurrency(
    transaction: Transaction,
    accounts: List<Account>,
    baseCurrency: String
): Option<String> {
    val account = accounts.find {
        it.id == transaction.getAccountId()
    }
        ?: return baseCurrency.toOption()
    return accountCurrency(account, baseCurrency).toOption()
}

@Deprecated("Uses legacy Transaction")
object LegacyTrnFunctions {
    @Pure
    fun expenses(transactions: List<com.ivy.base.legacy.Transaction>): List<com.ivy.base.legacy.Transaction> {
        return transactions.filter { it.type == TransactionType.EXPENSE }
    }

    @Pure
    fun incomes(transactions: List<com.ivy.base.legacy.Transaction>): List<com.ivy.base.legacy.Transaction> {
        return transactions.filter { it.type == TransactionType.INCOME }
    }

    @Pure
    fun trnCurrency(
        transaction: com.ivy.base.legacy.Transaction,
        accounts: List<Account>,
        baseCurrency: String
    ): Option<String> {
        val account = accounts.find { it.id == transaction.accountId }
            ?: return baseCurrency.toOption()
        return accountCurrency(account, baseCurrency).toOption()
    }
}
