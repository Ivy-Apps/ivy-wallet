package com.ivy.reports.extensions

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
import com.ivy.reports.BuildConfig
import com.ivy.reports.FilterUiState
import com.ivy.reports.HeaderUiState
import com.ivy.reports.ReportUiState
import com.ivy.reports.data.ReportFilterState
import com.ivy.reports.data.SelectableAccount
import com.ivy.reports.data.SelectableReportsCategory
import com.ivy.reports.template.TemplateDataHolder
import kotlinx.coroutines.flow.StateFlow

/** ---------------------------------------- Flows -----------------------------------------------*/

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
fun reportEmptyTrnsList() = EmptyState(
    title = stringResource(R.string.no_filter),
    description = stringResource(R.string.invalid_filter_warning)
)

fun emptyReportUiState(baseCurrency: CurrencyCode): ReportUiState {
    return ReportUiState(
        baseCurrency = baseCurrency,
        loading = false,

        headerUiState = emptyHeaderUiState().toImmutableItem(),
        trnsList = ImmutableData(emptyTransactionList()),

        filterVisible = false,
        filterUiState = emptyFilterUiState(),

        templateDataHolder = TemplateDataHolder.empty()
    )
}

fun emptyHeaderUiState() = HeaderUiState(
    balance = 0.0,

    income = 0.0,
    expenses = 0.0,

    incomeTransactionsCount = 0,
    expenseTransactionsCount = 0,

    accountIdFilters = ImmutableData(emptyList()),
    transactionsOld = ImmutableData(emptyList()),
    treatTransfersAsIncExp = false
)

fun emptyTransactionList() = TransactionsList(null, null, emptyList())

fun emptyFilterUiState() = FilterUiState(
    selectedTrnTypes = ImmutableData(emptyList()),
    period = ImmutableData(null),
    selectedAcc = ImmutableData(emptyList()),
    selectedCat = ImmutableData(emptyList()),
    minAmount = null,
    maxAmount = null,
    includeKeywords = ImmutableData(emptyList()),
    excludeKeywords = ImmutableData(emptyList()),
    selectedPlannedPayments = ImmutableData(emptyList()),
    treatTransfersAsIncExp = false
)

/** ---------------------------------- Utility Functions -----------------------------------------*/

fun ExpandCollapseHandler.expand() = this.setExpanded(true)
fun ExpandCollapseHandler.collapse() = this.setExpanded(false)

fun SelectableAccount.switchSelected() = this.copy(selected = !this.selected)
fun SelectableReportsCategory.switchSelected() = this.copy(selected = !this.selected)

fun ReportFilterState.isFilterValid() = when {
    selectedTrnTypes.isEmpty() -> false

    period == null -> false

    selectedAccounts.none { it.selected } -> false

    selectedCategories.none { it.selected } -> false

    minAmount != null && maxAmount != null -> when {
        minAmount > maxAmount -> false
        maxAmount < minAmount -> false
        else -> true
    }

    else -> true
}