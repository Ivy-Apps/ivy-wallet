package com.ivy.home

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerH
import com.ivy.design.l1_buildingBlocks.DividerSize
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.home.components.Balance
import com.ivy.home.components.BalanceMini
import com.ivy.home.components.IncomeExpense
import com.ivy.home.components.MoreMenuButton
import com.ivy.home.event.HomeEvent
import com.ivy.home.state.HomeStateUi
import kotlinx.coroutines.launch

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
        contentBelowTrns = {
            item {
                // TODO: Change that to 300.dp when we have transactions
                SpacerVer(height = 600.dp)
            }
        }
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
    stickyHeader(
        period = period,
        periodModal = periodModal,
        balance = balance,
        listState = listState,
        onBalanceClick = onBalanceClick
    )
    item {
        SpacerVer(height = 4.dp)
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
    balance: ValueUi,
    listState: LazyListState,
    onBalanceClick: () -> Unit
) {
    stickyHeader(
        key = "home_tab_header",
        contentType = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(UI.colors.pure)
                .padding(top = 12.dp, bottom = 4.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (period != null) {
                PeriodButton(
                    selectedPeriod = period,
                    periodModal = periodModal
                )
            }
            SpacerWeight(weight = 1f)
            SpacerWeight(weight = 1f)
            MoreMenuButton {
                // TODO: Implement
            }
        }

        val headerCollapsed by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 1 }
        }
        AnimatedVisibility(
            modifier = Modifier.background(UI.colors.pure),
            visible = headerCollapsed,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            val coroutineScope = rememberCoroutineScope()
            CollapsedToolbarExtension(balance = balance,
                onBalanceClick = onBalanceClick,
                onScrollToTop = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            )
        }
    }
}

@Composable
private fun CollapsedToolbarExtension(
    balance: ValueUi,
    onBalanceClick: () -> Unit,
    onScrollToTop: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceMini(
                balance = balance,
                onClick = onBalanceClick
            )
            SpacerWeight(weight = 1f)
            IvyButton(
                size = ButtonSize.Small,
                visibility = ButtonVisibility.Low,
                feeling = ButtonFeeling.Positive,
                text = "Scroll to top",
                icon = null,
                onClick = onScrollToTop,
            )
        }
        SpacerVer(height = 4.dp)
        DividerH(size = DividerSize.FillMax(padding = 0.dp))
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