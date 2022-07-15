package com.ivy.reports

import com.ivy.data.Account
import com.ivy.data.Category
import com.ivy.data.transaction.Transaction
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
    val upcomingTransactions: List<Transaction> = emptyList(),
    val overdueTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val upcomingExpanded: Boolean = false,
    val overdueExpanded: Boolean = false,
    val filter: ReportFilter? = null,
    val loading: Boolean = false,
    val accountIdFilters: List<UUID> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val filterOverlayVisible: Boolean = false,
    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false
)