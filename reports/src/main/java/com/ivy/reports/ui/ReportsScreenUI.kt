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
import com.ivy.reports.HeaderState
import com.ivy.reports.ImmutableData
import com.ivy.reports.ReportsEvent
import com.ivy.reports.reportsTrnsListEmptyState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxWithConstraintsScope.ReportsScreenUI(
    baseCurrency: CurrencyCode,
    headerState: ImmutableData<HeaderState>,
    trnsList: ImmutableData<TransactionsList>,
    onEvent: (ReportsEvent) -> Unit = {}
) {
    trnsList.data
        .TrnsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            scrollStateKey = "Reports",
            emptyState = reportsTrnsListEmptyState(),
            contentAboveTrns = {
                stickyHeader {
                    ReportsToolBar(onEventHandler = onEvent)
                }

                item {
                    ReportsHeader(
                        baseCurrency = baseCurrency,
                        headerState = headerState,
                        onEventHandler = onEvent
                    )
                }
            }
        )
}