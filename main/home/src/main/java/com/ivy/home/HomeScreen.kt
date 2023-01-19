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
import com.ivy.core.ui.time.PeriodButton
import com.ivy.core.ui.time.PeriodModal
import com.ivy.core.ui.transaction.TransactionsLazyColumn
import com.ivy.core.ui.transaction.rememberTransactionsListState
import com.ivy.core.ui.transaction.sampleTrnListItems
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.DividerSize
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.consumeClicks
import com.ivy.home.components.Balance
import com.ivy.home.components.BalanceMini
import com.ivy.home.components.IncomeExpense
import com.ivy.home.components.MoreMenuButton
import com.ivy.home.modal.AddTransactionModal
import com.ivy.main.bottombar.MainBottomBar
import com.ivy.main.bottombar.Tab
import com.ivy.wallet.utils.horizontalSwipeListener
import kotlinx.coroutines.launch

@Composable
fun BoxScope.HomeScreen() {
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

    val trnsListState = rememberTransactionsListState(
        scrollStateKey = "home_tab"
    )
    TransactionsLazyColumn(
        modifier = Modifier
            .systemBarsPadding()
            .horizontalSwipeListener(
                sensitivity = 200,
                onSwipeLeft = {
                    onEvent(HomeEvent.BottomBar.AccountsClick)
                },
                onSwipeRight = {
                    onEvent(HomeEvent.BottomBar.AccountsClick)
                }
            ),
        items = state.trnListItems,
        state = trnsListState,
        contentAboveTrns = { listState ->
            header(
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
                onEvent(HomeEvent.BottomBar.Hide)
            } else {
                onEvent(HomeEvent.BottomBar.Show)
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()
    MainBottomBar(
        visible = state.bottomBarVisible,
        selectedTab = Tab.Home,
        onActionClick = { onEvent(HomeEvent.BottomBar.AddClick) },
        onHomeClick = {
            // Scroll to top
            coroutineScope.launch {
                trnsListState.listState.animateScrollToItem(0)
            }
        },
        onAccountsClick = { onEvent(HomeEvent.BottomBar.AccountsClick) }
    )

    Modals(
        periodModal = periodModal,
        addTransactionModal = state.addTransactionModal,
        onEvent = onEvent,
    )
}

// region Header
fun LazyListScope.header(
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
                .padding(horizontal = 16.dp)
                .consumeClicks(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PeriodButton(periodModal = periodModal)
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
            CollapsedToolbarExtension(
                balance = balance,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .consumeClicks()
    ) {
        Row(
            modifier = Modifier.padding(
                start = 24.dp, end = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BalanceMini(
                balance = balance,
                onClick = onBalanceClick
            )
            SpacerWeight(weight = 1f)
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.Low,
                feeling = Feeling.Positive,
                text = "Go to top",
                typo = UI.typo.c,
                onClick = onScrollToTop,
            )
        }
        SpacerVer(height = 4.dp)
        DividerHor(size = DividerSize.FillMax(padding = 0.dp))
    }
}
// endregion

// region Modals
@Composable
private fun BoxScope.Modals(
    periodModal: IvyModal,
    addTransactionModal: IvyModal,
    onEvent: (HomeEvent) -> Unit,
) {
    PeriodModal(
        modal = periodModal,
    )

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
                balance = ValueUi("10,000.00", "USD"),
                income = ValueUi("1,500.35", "USD"),
                expense = ValueUi("3,000.50", "USD"),
                hideBalance = false,
                trnListItems = sampleTrnListItems(),
                bottomBarVisible = true,
                addTransactionModal = rememberIvyModal()
            ),
            onEvent = {}
        )
    }
}
// endregion