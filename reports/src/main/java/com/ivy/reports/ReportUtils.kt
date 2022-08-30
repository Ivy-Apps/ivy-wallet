package com.ivy.reports

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ivy.core.ui.transaction.EmptyState
import com.ivy.core.ui.transaction.ExpandCollapseHandler
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList
import java.util.*

fun ExpandCollapseHandler.expand() = this.setExpanded(true)
fun ExpandCollapseHandler.collapse() = this.setExpanded(false)

fun emptyTransactionList() = TransactionsList(null, null, emptyList())

@Composable
fun reportsEmptyState() = EmptyState(
    title = stringResource(com.ivy.base.R.string.no_filter),
    description = stringResource(com.ivy.base.R.string.invalid_filter_warning)
)

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Log.d(tag, "Compositions: $msg ${ref.value}")
    }
}

fun emptyReportFilter(
    baseCurrency: CurrencyCode
) = ReportFilter(
    id = UUID.randomUUID(),
    trnTypes = emptyList(),
    period = null,
    accounts = emptyList(),
    categories = emptyList(),
    currency = baseCurrency,
    includeKeywords = emptyList(),
    excludeKeywords = emptyList(),
    minAmount = null,
    maxAmount = null
)

fun emptyReportScreenState(baseCurrency: CurrencyCode): ReportScreenState {
    return ReportScreenState(
        baseCurrency = baseCurrency,
        balance = 0.0,

        income = 0.0,
        incomeTransactionsCount = 0,

        expenses = 0.0,
        expenseTransactionsCount = 0,

        accounts = emptyList(),
        categories = emptyList(),

        showTransfersAsIncExpCheckbox = false,
        treatTransfersAsIncExp = false,

        filter = emptyReportFilter(baseCurrency),
        trnsList = emptyTransactionList(),

        loading = false,
        filterOptionsVisibility = false,

        accountIdFilters = emptyList(),
        transactionsOld = emptyList()
    )
}

