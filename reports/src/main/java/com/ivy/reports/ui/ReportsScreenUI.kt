package com.ivy.reports.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivy.core.ui.transaction.TrnsLazyColumn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionsList
import com.ivy.reports.ImmutableData
import com.ivy.reports.ReportScreenEvent
import com.ivy.reports.reportsEmptyState
import com.ivy.reports.states.HeaderState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxWithConstraintsScope.ReportsScreenUI(
    baseCurrency: CurrencyCode,
    headerState: HeaderState,
    trnsList: ImmutableData<TransactionsList>,
    onEvent: (ReportScreenEvent) -> Unit = {}
) {
    trnsList.data
        .TrnsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            scrollStateKey = "Reports",
            emptyState = reportsEmptyState(),
            contentAboveTrns = {
                stickyHeader {
                    ReportsToolBar(onEventHandler = onEvent)
                }

                item {
                    ReportsHeader(
                        baseCurrency = baseCurrency,
                        state = headerState,
                        onEventHandler = onEvent
                    )
                }
            }
        )
}