package com.ivy.reports

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ivy.base.R
import com.ivy.core.ui.transaction.EmptyState
import com.ivy.core.ui.transaction.ExpandCollapseHandler
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
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

        headerState = emptyHeaderState().toImmutableItem(),
        trnsList = ImmutableData(emptyTransactionList()),

        filterVisible = false,
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
    selectedTrnTypes = ImmutableData(emptyList()),
    period = ImmutableData(null),
    selectedAcc = ImmutableData(emptyList()),
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
@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, R> combineMultiple(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    flow,
    flow2,
    flow3,
    flow4,
    flow5,
    flow6
) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6
    )
}

@Composable
fun <T> rememberStateWithLifecycle(
    stateFlow: StateFlow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> {
    val initialValue = remember(stateFlow) { stateFlow.value }
    return produceState(
        key1 = stateFlow,
        key2 = lifecycle,
        key3 = minActiveState,
        initialValue = initialValue
    ) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            stateFlow.collect {
                this@produceState.value = it
            }
        }
    }
}