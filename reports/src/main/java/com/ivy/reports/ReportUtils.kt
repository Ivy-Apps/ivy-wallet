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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

/**
 * Returns a [Flow] whose values are generated with [transform] function by combining
 * the most recently emitted values by each flow.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> combineMultiple(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R
): Flow<R> = combine(
    flow,
    flow2,
    flow3,
    flow4,
    flow5,
    flow6,
    flow7,
    flow8,
    flow9,
    flow10,
    flow11,
    flow12
) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
        args[6] as T7,
        args[7] as T8,
        args[8] as T9,
        args[9] as T10,
        args[10] as T11,
        args[11] as T12,
    )
}