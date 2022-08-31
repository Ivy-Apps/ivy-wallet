package com.ivy.reports.states

import androidx.compose.runtime.Immutable
import com.ivy.data.transaction.TransactionOld
import java.util.*

@Immutable
data class HeaderState(
    val balance: Double,

    val income: Double,
    val expenses: Double,

    val incomeTransactionsCount: Int,
    val expenseTransactionsCount: Int,

    val showTransfersAsIncExpCheckbox: Boolean,
    val treatTransfersAsIncExp: Boolean,

    // TODO(Reports): Need to remove the variables below, Kept for Reports->PieChart Screen Compatibility
    val accountIdFilters: List<UUID>,
    val transactionsOld: List<TransactionOld>,
)