package com.ivy.reports

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ivy.core.ui.transaction.EmptyState
import com.ivy.core.ui.transaction.ExpandCollapseHandler
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList
import com.ivy.reports.states.FilterState
import com.ivy.reports.states.HeaderState

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

//fun emptyReportFilter(
//    baseCurrency: CurrencyCode
//) = ReportFilter(
//    id = UUID.randomUUID(),
//    trnTypes = emptyList(),
//    period = null,
//    accounts = emptyList<Account>().toImmutableList(),
//    categories = emptyList(),
//    currency = baseCurrency,
//    includeKeywords = emptyList(),
//    excludeKeywords = emptyList(),
//    minAmount = null,
//    maxAmount = null
//)

fun emptyReportScreenState(baseCurrency: CurrencyCode): ReportScreenState {
    return ReportScreenState(
        baseCurrency = baseCurrency,

        filterState = emptyFilterState(),
        trnsList = ImmutableData(emptyTransactionList()),

        loading = false,

        headerState = emptyHeaderState()
    )
}

fun emptyHeaderState() = HeaderState(
    0.0, 0.0, 0.0, 0, 0, false, false,
    emptyList(), emptyList()
)

fun emptyFilterState() = FilterState(
    visible = false,
    selectedTrnTypes = ImmutableData(emptyList()),
    period = ImmutableData(null),
    allAccounts = ImmutableData(emptyList()),
    selectedAcc = ImmutableData(emptyList()),
    allCategories = ImmutableData(emptyList()),
    selectedCat = ImmutableData(emptyList()),
    minAmount = null,
    maxAmount = null,
    includeKeywords = ImmutableData(emptyList()),
    excludeKeywords = ImmutableData(emptyList()),
)

@Immutable
data class ImmutableData<T>(val data: T)
fun <T> T.toImmutableItem(): ImmutableData<T> = ImmutableData(this)

//@Stable
//fun interface StableClickListener<T, K> {
//    fun onClick(first: T, second: K)
//}

@Immutable
fun interface StableClickListener<T> {
    fun onClick(first: T)
}

