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
import com.ivy.core.ui.data.period.dummyRangeUi
import com.ivy.core.ui.time.PeriodButton
import com.ivy.core.ui.time.PeriodModal
import com.ivy.core.ui.transaction.TransactionsLazyColumn
import com.ivy.core.ui.transaction.sampleTransactionListUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.DividerSize
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.home.components.Balance
import com.ivy.home.components.BalanceMini
import com.ivy.home.components.IncomeExpense
import com.ivy.home.components.MoreMenuButton
import com.ivy.home.modal.AddTransactionModal
import com.ivy.home.state.HomeStateUi
import com.ivy.menu.HomeMoreMenu
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
                onMoreClick = { onEvent(HomeEvent.MoreClick) }
            )
        },
        contentBelowTrns = {
            item {
                // TODO: Change that to 300.dp when we have transactions
                SpacerVer(height = 600.dp)
            }
        },
        onFirstVisibleItemChange = { firstVisibleItemIndex ->
            if (firstVisibleItemIndex > 0) {
                onEvent(HomeEvent.HideBottomBar)
            } else {
                onEvent(HomeEvent.ShowBottomBar)
            }
        }
    )

    HomeMoreMenu(
        visible = state.moreMenuVisible,
        onMenuClose = {
            onEvent(HomeEvent.MoreClick)
        }
    )

    Modals(
        periodModal = periodModal,
        selectedPeriod = state.period,
        addTransactionModal = state.addTransactionModal,
        onEvent = onEvent,
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
    onMoreClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
) {
    toolbar(
        period = period,
        periodModal = periodModal,
        balance = balance,
        listState = listState,
        onBalanceClick = onBalanceClick,
        onMoreClick = onMoreClick,
    )
    item(key = "home_header_balance") {
        SpacerVer(height = 4.dp)
        Balance(balance = balance, onClick = onBalanceClick)
    }
    item(key = "home_header_income_expense") {
        SpacerVer(height = 4.dp)
        IncomeExpense(
            income = income,
            expense = expense,
            onIncomeClick = onIncomeClick,
            onExpenseClick = onExpenseClick,
        )
    }
    item(key = "header_divider_line") {
        SpacerVer(height = 24.dp)
        DividerHor()
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.toolbar(
    period: SelectedPeriodUi?,
    periodModal: IvyModal,
    balance: ValueUi,
    listState: LazyListState,
    onBalanceClick: () -> Unit,
    onMoreClick: () -> Unit = {},
) {
    stickyHeader(
        key = "home_tab_toolbar",
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
            MoreMenuButton(onClick = onMoreClick)
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
        BalanceMini(
            balance = balance,
            onClick = onBalanceClick
        )
        SpacerVer(height = 4.dp)
        DividerHor(size = DividerSize.FillMax(padding = 0.dp))
    }
}
// endregion

// region Modals
@Composable
private fun BoxScope.Modals(
    periodModal: IvyModal,
    selectedPeriod: SelectedPeriodUi?,
    addTransactionModal: IvyModal,
    onEvent: (HomeEvent) -> Unit,
) {
    if (selectedPeriod != null) {
        PeriodModal(
            modal = periodModal,
            selectedPeriod = selectedPeriod
        )
    }

    AddTransactionModal(
        modal = addTransactionModal,
        onAddTransfer = {
            onEvent(HomeEvent.AddTransfer)
        },
        onAddIncome = {
            onEvent(HomeEvent.AddIncome)
        },
        onAddExpense = {
            onEvent(HomeEvent.AddExpense)
        }
    )
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
                    rangeUi = dummyRangeUi()
                ),
                balance = ValueUi("10,000.00", "USD"),
                income = ValueUi("1,500.35", "USD"),
                expense = ValueUi("3,000.50", "USD"),
                hideBalance = false,
                moreMenuVisible = false,
                trnsList = sampleTransactionListUi(),
                addTransactionModal = rememberIvyModal()
            ),
            onEvent = {}
        )
    }
}
// endregion