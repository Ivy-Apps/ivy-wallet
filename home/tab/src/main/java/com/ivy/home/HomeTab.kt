package com.ivy.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.dummyMonthUi
import com.ivy.core.ui.data.period.dummyPeriodUi
import com.ivy.core.ui.time.PeriodButton
import com.ivy.core.ui.time.PeriodModal
import com.ivy.core.ui.transaction.TransactionsLazyColumn
import com.ivy.core.ui.transaction.sampleTransactionListUi
import com.ivy.design.l1_buildingBlocks.DividerH
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.home.components.Balance
import com.ivy.home.components.IncomeExpense
import com.ivy.home.event.HomeEvent
import com.ivy.home.state.HomeStateUi

@Composable
fun BoxScope.HomeTab() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    UI(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun BoxScope.UI(
    state: HomeStateUi,
    onEvent: (HomeEvent) -> Unit,
) {
    val periodModal = rememberIvyModal()

    TransactionsLazyColumn(
        modifier = Modifier.systemBarsPadding(),
        transactionsList = state.trnsList,
        scrollStateKey = "home_tab",
        contentAboveTrns = { listState ->
            header(
                period = state.period,
                periodModal = periodModal,
                balance = state.balance,
                income = state.income,
                expense = state.expense,
                listState = listState,
                onBalanceClick = { onEvent(HomeEvent.BalanceClick) },
                onIncomeClick = { onEvent(HomeEvent.IncomeClick) },
                onExpenseClick = { onEvent(HomeEvent.ExpenseClick) },
            )
        },
    )

    Modals(
        periodModal = periodModal,
        selectedPeriod = state.period
    )
}

// region Header
fun LazyListScope.header(
    period: SelectedPeriodUi?,
    periodModal: IvyModal,
    balance: ValueUi,
    income: ValueUi,
    expense: ValueUi,
    listState: LazyListState,
    onBalanceClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
) {
    stickyHeader(period, periodModal, listState)
    item {
        SpacerVer(height = 12.dp)
        Balance(balance = balance, onClick = onBalanceClick)
    }
    item {
        SpacerVer(height = 4.dp)
        IncomeExpense(
            income = income,
            expense = expense,
            onIncomeClick = onIncomeClick,
            onExpenseClick = onExpenseClick,
        )
    }
    item {
        SpacerVer(height = 24.dp)
        DividerH()
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.stickyHeader(
    period: SelectedPeriodUi?,
    periodModal: IvyModal,
    listState: LazyListState
) {
    stickyHeader(
        key = "home_tab_header",
        contentType = null
    ) {
        SpacerVer(height = 12.dp)
        if (period != null) {
            PeriodButton(
                modifier = Modifier.padding(start = 16.dp),
                selectedPeriod = period,
                periodModal = periodModal
            )
        }

        val balanceNotVisible by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 2 }
        }
        if (balanceNotVisible) {
            SpacerVer(height = 8.dp)
            DividerH()
        }
    }
}


// endregion

// region Modals
@Composable
private fun BoxScope.Modals(
    periodModal: IvyModal,
    selectedPeriod: SelectedPeriodUi?
) {
    if (selectedPeriod != null) {
        PeriodModal(
            modal = periodModal,
            selectedPeriod = selectedPeriod
        )
    }
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = HomeStateUi(
                period = SelectedPeriodUi.Monthly(
                    btnText = "Sep",
                    month = dummyMonthUi(),
                    periodUi = dummyPeriodUi()
                ),
                balance = ValueUi("10,000.00", "USD"),
                income = ValueUi("1,500.35", "USD"),
                expense = ValueUi("3,000.50", "USD"),
                hideBalance = false,
                trnsList = sampleTransactionListUi()
            ),
            onEvent = {}
        )
    }
}
// endregion