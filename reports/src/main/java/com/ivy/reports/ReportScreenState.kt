package com.ivy.reports

import androidx.compose.runtime.Stable
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import java.util.*

@Stable
data class ReportScreenState(
    val baseCurrency: String = "",
    val balance: Double = 0.0,

    val income: Double = 0.0,
    val incomeTransactionsCount: Int = 0,

    val expenses: Double = 0.0,
    val expenseTransactionsCount: Int = 0,

    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),

    val upcomingPayments: PlannedPaymentsStats = PlannedPaymentsStats.empty(),
    val overduePayments: PlannedPaymentsStats = PlannedPaymentsStats.empty(),

    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false,

    val filter: ReportFilter? = null,
    val transactionsWithDateDividers: TransactionsList = emptyTransactionList(),

    val loading: Boolean = false,
    val filterOverlayVisible: Boolean = false,

    // TODO(Reports): Need to remove the variables below, Kept for Reports->PieChart Screen Compatibility
    val accountIdFilters: List<UUID> = emptyList(),
    val transactionsOld: List<TransactionOld> = emptyList(),
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