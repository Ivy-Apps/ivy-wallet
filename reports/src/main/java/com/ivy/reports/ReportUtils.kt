package com.ivy.reports

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ivy.base.R
import com.ivy.core.ui.transaction.EmptyState
import com.ivy.core.ui.transaction.ExpandCollapseHandler
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList

/** ------------------------------------ Compose UI Logging --------------------------------------*/

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@SuppressLint("LogNotTimber")
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun LogCompositions(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Log.d(tag, "Compositions: $msg ${ref.value}")
    }
}

/** --------------------------------------- Immutability -----------------------------------------*/

@Immutable
data class ImmutableData<T>(val data: T)

fun <T> T.toImmutableItem(): ImmutableData<T> = ImmutableData(this)

/** --------------------------------------- Empty States -----------------------------------------*/

@Composable
fun reportsTrnsListEmptyState() = EmptyState(
    title = stringResource(R.string.no_filter),
    description = stringResource(R.string.invalid_filter_warning)
)

fun emptyReportScreenState(baseCurrency: CurrencyCode): ReportState {
    return ReportState(
        baseCurrency = baseCurrency,
        loading = false,

        headerState = emptyHeaderState(),
        trnsList = ImmutableData(emptyTransactionList()),

        filterState = emptyFilterState()
    )
}

fun emptyHeaderState() = HeaderState(
    balance = 0.0,

    income = 0.0,
    expenses = 0.0,

    incomeTransactionsCount = 0,
    expenseTransactionsCount = 0,

    showTransfersAsIncExpCheckbox = false,
    treatTransfersAsIncExp = false,

    accountIdFilters = ImmutableData(emptyList()),
    transactionsOld = ImmutableData(emptyList())
)

fun emptyTransactionList() = TransactionsList(null, null, emptyList())

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
    selectedPlannedPayments = ImmutableData(emptyList())
)

/** ---------------------------------- Utility Functions -----------------------------------------*/

fun ExpandCollapseHandler.expand() = this.setExpanded(true)
fun ExpandCollapseHandler.collapse() = this.setExpanded(false)