package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList
import com.ivy.reports.states.FilterState
import com.ivy.reports.states.HeaderState

@Immutable
data class ReportScreenState(
    val baseCurrency: CurrencyCode,

    val headerState: HeaderState,
    val filterState: FilterState,
    val trnsList: TransactionsList,

    val loading: Boolean
)