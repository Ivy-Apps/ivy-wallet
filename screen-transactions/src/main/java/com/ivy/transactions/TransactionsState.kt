package com.ivy.transactions

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TransactionsState(
    val period: TimePeriod,
    val baseCurrency: String,
    val currency: String,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val account: Account?,
    val category: Category?,
    val balance: Double,
    val balanceBaseCurrency: Double?,
    val income: Double,
    val expenses: Double,
    val initWithTransactions: Boolean,
    val treatTransfersAsIncomeExpense: Boolean,
    val history: ImmutableList<TransactionHistoryItem>,
    val upcoming: ImmutableList<Transaction>,
    val upcomingExpanded: Boolean,
    val upcomingIncome: Double,
    val upcomingExpenses: Double,
    val overdue: ImmutableList<Transaction>,
    val overdueExpanded: Boolean,
    val overdueIncome: Double,
    val overdueExpenses: Double
)