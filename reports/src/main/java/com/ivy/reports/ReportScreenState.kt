package com.ivy.reports

import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnHistoryItem
import java.util.*

data class ReportScreenState(
    val baseCurrency: String = "",
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val upcomingIncome: Double = 0.0,
    val upcomingExpenses: Double = 0.0,
    val overdueIncome: Double = 0.0,
    val overdueExpenses: Double = 0.0,
    val history: List<Any> = emptyList(),
    val upcomingTransactions: List<TransactionOld> = emptyList(),
    val overdueTransactions: List<TransactionOld> = emptyList(),
    val categories: List<CategoryOld> = emptyList(),
    val accounts: List<AccountOld> = emptyList(),
    val upcomingExpanded: Boolean = false,
    val overdueExpanded: Boolean = false,
    val filter: ReportFilter? = null,
    val loading: Boolean = false,
    val accountIdFilters: List<UUID> = emptyList(),
    val transactions: List<TransactionOld> = emptyList(),
    val filterOverlayVisible: Boolean = false,
    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false,


    val accountsNew: List<Account> = emptyList(),
    val categoriesNew: List<Category> = emptyList(),
    val upcomingPayments : PlannedPaymentsStats = PlannedPaymentsStats.empty(),
    val overduePayments : PlannedPaymentsStats = PlannedPaymentsStats.empty(),
    val transactionsWithDateDividers : List<TrnHistoryItem> = emptyList(),
)

data class ReportScreenStateNew(
    val baseCurrency: String = "",
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val upcomingIncome: Double = 0.0,
    val upcomingExpenses: Double = 0.0,
    val overdueIncome: Double = 0.0,
    val overdueExpenses: Double = 0.0,
    val history: List<Any> = emptyList(),
    val upcomingTransactions: List<TransactionOld> = emptyList(),
    val overdueTransactions: List<TransactionOld> = emptyList(),
    val categories: List<CategoryOld> = emptyList(),

    val upcomingExpanded: Boolean = false,
    val overdueExpanded: Boolean = false,
    val filter: ReportFilter? = null,
    val loading: Boolean = false,
    val accountIdFilters: List<UUID> = emptyList(),
    val transactions: List<TransactionOld> = emptyList(),
    val filterOverlayVisible: Boolean = false,
    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false
)

data class PlannedPaymentsStats(
    val income: Double,
    val expenses: Double,
    val transactions: List<Transaction>
) {
    companion object {
        fun empty() = PlannedPaymentsStats(0.0, 0.0, emptyList())
    }
}