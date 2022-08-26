package com.ivy.reports

import androidx.compose.runtime.Stable
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import java.util.*

@Stable
data class ReportScreenState(
    val baseCurrency: CurrencyCode = "",
    val balance: Double = 0.0,

    val income: Double = 0.0,
    val incomeTransactionsCount: Int = 0,

    val expenses: Double = 0.0,
    val expenseTransactionsCount: Int = 0,

    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),

    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false,

    val filter: ReportFilter? = null,
    val transactionsWithDateDividers: TransactionsList = emptyTransactionList(),

    val loading: Boolean = false,
    val filterOptionsVisibility: Boolean = false,

    // TODO(Reports): Need to remove the variables below, Kept for Reports->PieChart Screen Compatibility
    val accountIdFilters: List<UUID> = emptyList(),
    val transactionsOld: List<TransactionOld> = emptyList(),
)