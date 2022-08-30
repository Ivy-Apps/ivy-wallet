package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TransactionsList
import java.util.*

@Immutable
data class ReportScreenState(
    val baseCurrency: CurrencyCode,
    val balance: Double,

    val income: Double,
    val incomeTransactionsCount: Int,

    val expenses: Double,
    val expenseTransactionsCount: Int,

    val accounts: List<Account>,
    val categories: List<Category>,

    val showTransfersAsIncExpCheckbox: Boolean,
    val treatTransfersAsIncExp: Boolean,

    val filter: ReportFilter,
    val trnsList: TransactionsList,

    val loading: Boolean,
    val filterOptionsVisibility: Boolean,

    // TODO(Reports): Need to remove the variables below, Kept for Reports->PieChart Screen Compatibility
    val accountIdFilters: List<UUID>,
    val transactionsOld: List<TransactionOld>,
)