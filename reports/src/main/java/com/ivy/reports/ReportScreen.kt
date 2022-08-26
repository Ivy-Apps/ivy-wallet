package com.ivy.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.R
import com.ivy.core.ui.transaction.defaultExpandCollapseHandler
import com.ivy.core.ui.transaction.dummyDueActions
import com.ivy.core.ui.transaction.transactionsList
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.reports.ui.ReportsFilterOptions
import com.ivy.reports.ui.ReportsHeader
import com.ivy.reports.ui.ReportsLoadingScreen
import com.ivy.reports.ui.ReportsToolBar
import com.ivy.screens.Report

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreen(
    screen: Report
) {
    val viewModel: ReportViewModel = viewModel()
    val state by viewModel.state().collectAsState()

    onScreenStart {
        viewModel.onEvent(ReportScreenEvent.Start)
    }

    UI(
        state = state,
        onEventHandler = viewModel::onEvent
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: ReportScreenState = ReportScreenState(),
    onEventHandler: (ReportScreenEvent) -> Unit = {}
) {
    val upcomingHandler = defaultExpandCollapseHandler()
    val overdueHandler = defaultExpandCollapseHandler()
    val emptyState = reportsEmptyState()

    ReportsLoadingScreen(visible = state.loading, text = stringResource(R.string.generating_report))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        stickyHeader {
            ReportsToolBar(onEventHandler = onEventHandler)
        }

        item {
            ReportsHeader(state = state, onEventHandler = onEventHandler)
        }

        transactionsList(
            trnsList = state.transactionsWithDateDividers,
            upcomingHandler = upcomingHandler,
            overdueHandler = overdueHandler,
            dueActions = dummyDueActions(),
            emptyState = emptyState
        )
    }

    ReportsFilterOptions(
        baseCurrency = state.baseCurrency,
        visible = state.filterOverlayVisible,
        filter = state.filter,
        accounts = state.accounts,
        categories = state.categories,
        onClose = {
            onEventHandler.invoke(
                ReportScreenEvent.OnFilterOverlayVisible(
                    filterOverlayVisible = false
                )
            )
        },
        onSetFilter = {
            onEventHandler(ReportScreenEvent.OnFilter(filter = it))
        }
    )
}

//@Composable
//private fun NoFilterEmptyState(
//    setFilterOverlayVisible: (Boolean) -> Unit
//) {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(Modifier.height(16.dp))
//
//        IvyIcon(
//            icon = R.drawable.ic_filter_l,
//            tint = Gray
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            text = stringResource(R.string.no_filter),
//            style = UI.typo.b1.style(
//                color = Gray,
//                fontWeight = FontWeight.ExtraBold
//            )
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            modifier = Modifier.padding(horizontal = 32.dp),
//            text = stringResource(R.string.invalid_filter_warning),
//            style = UI.typo.b2.style(
//                color = Gray,
//                fontWeight = FontWeight.Medium,
//                textAlign = TextAlign.Center
//            )
//        )
//
//        Spacer(Modifier.height(32.dp))
//
//        IvyButton(
//            iconStart = R.drawable.ic_filter_xs,
//            text = stringResource(R.string.set_filter)
//        ) {
//            setFilterOverlayVisible(true)
//        }
//
//        Spacer(Modifier.height(96.dp))
//    }
//}

//@Composable
//private fun Toolbar(
//    onExport: () -> Unit,
//    onFilter: () -> Unit
//) {
//    val nav = navigation()
//    IvyToolbar(
//        backButtonType = BackButtonType.CLOSE,
//        onBack = {
//            nav.back()
//        }
//    ) {
//        Spacer(Modifier.weight(1f))
//
//        //Export CSV
//        IvyOutlinedButton(
//            text = stringResource(R.string.export),
//            iconTint = Green,
//            textColor = Green,
//            solidBackground = true,
//            padding = 8.dp,
//            iconStart = R.drawable.ic_export_csv
//        ) {
//            onExport()
//        }
//
//        Spacer(Modifier.width(16.dp))
//
//        //Filter
//        CircleButtonFilled(
//            icon = R.drawable.ic_filter_xs
//        ) {
//            onFilter()
//        }
//
//        Spacer(Modifier.width(24.dp))
//    }
//}

//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview() {
//    com.ivy.core.ui.temp.Preview {
//        val acc1 = AccountOld("Cash", color = Green.toArgb())
//        val acc2 = AccountOld("DSK", color = GreenDark.toArgb())
//        val cat1 = CategoryOld("Science", color = Purple1Dark.toArgb(), icon = "atom")
//        val state = ReportScreenState(
//            baseCurrency = "BGN",
//            balance = -6405.66,
//            income = 2000.0,
//            expenses = 8405.66,
//            upcomingIncome = 4800.23,
//            upcomingExpenses = 0.0,
//            overdueIncome = 2335.12,
//            overdueExpenses = 0.0,
//            history = emptyList(),
//            upcomingTransactions = emptyList(),
//            overdueTransactions = emptyList(),
//
//            upcomingExpanded = true,
//            overdueExpanded = true,
//            filter = ReportFilter.emptyFilter("BGN"),
//            loading = false,
//            accounts = listOf(
//                acc1,
//                acc2,
//                AccountOld("phyre", color = GreenLight.toArgb(), icon = "cash"),
//                AccountOld("Revolut", color = IvyDark.toArgb()),
//            ),
//            categories = listOf(
//                cat1,
//                CategoryOld("Pet", color = Red3Light.toArgb(), icon = "pet"),
//                CategoryOld("Home", color = Green.toArgb(), icon = null),
//            ),
//        )
//
//        UI(state = state)
//    }
//}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview_NO_FILTER() {
//    com.ivy.core.ui.temp.Preview {
//        val acc1 = AccountOld("Cash", color = Green.toArgb())
//        val acc2 = AccountOld("DSK", color = GreenDark.toArgb())
//        val cat1 = CategoryOld("Science", color = Purple1Dark.toArgb(), icon = "atom")
//        val state = ReportScreenState(
//            baseCurrency = "BGN",
//            balance = 0.0,
//            income = 0.0,
//            expenses = 0.0,
//            upcomingIncome = 0.0,
//            upcomingExpenses = 0.0,
//            overdueIncome = 0.0,
//            overdueExpenses = 0.0,
//
//            history = emptyList(),
//            upcomingTransactions = emptyList(),
//            overdueTransactions = emptyList(),
//
//            upcomingExpanded = true,
//            overdueExpanded = true,
//
//            filter = null,
//            loading = false,
//
//            accounts = listOf(
//                acc1,
//                acc2,
//                AccountOld("phyre", color = GreenLight.toArgb(), icon = "cash"),
//                AccountOld("Revolut", color = IvyDark.toArgb()),
//            ),
//            categories = listOf(
//                cat1,
//                CategoryOld("Pet", color = Red3Light.toArgb(), icon = "pet"),
//                CategoryOld("Home", color = Green.toArgb(), icon = null),
//            ),
//        )
//
//        UI(state = state)
//    }
//}

//        if (state.filter != null) {
//            transactions(
//                baseData = AppBaseData(
//                    baseCurrency = state.baseCurrency,
//                    categories = state.categories,
//                    accounts = state.accounts,
//                ),
//
//                upcoming = DueSection(
//                    trns = state.upcomingTransactions,
//                    stats = IncomeExpensePair(
//                        income = state.upcomingIncome.toBigDecimal(),
//                        expense = state.upcomingExpenses.toBigDecimal()
//                    ),
//                    expanded = state.upcomingExpanded
//                ),
//
//                setUpcomingExpanded = {
//                    onEventHandler.invoke(ReportScreenEvent.OnUpcomingExpanded(upcomingExpanded = it))
//                },
//
//                overdue = DueSection(
//                    trns = state.overdueTransactions,
//                    stats = IncomeExpensePair(
//                        income = state.overdueIncome.toBigDecimal(),
//                        expense = state.overdueExpenses.toBigDecimal()
//                    ),
//                    expanded = state.overdueExpanded
//                ),
//                setOverdueExpanded = {
//                    onEventHandler.invoke(ReportScreenEvent.OnOverdueExpanded(overdueExpanded = it))
//                },
//
//                history = state.history,
//                lastItemSpacer = 48.dp,
//
//                onPayOrGet = {
//
//                },
//                emptyStateTitle = com.ivy.core.ui.temp.stringRes(R.string.no_transactions),
//                emptyStateText = com.ivy.core.ui.temp.stringRes(R.string.no_transactions_for_your_filter)
//            )
//        } else {
//            item {
//                NoFilterEmptyState(
//                    setFilterOverlayVisible = {
//                        onEventHandler.invoke(
//                            ReportScreenEvent.OnFilterOverlayVisible(
//                                filterOverlayVisible = it
//                            )
//                        )
//                    }
//                )
//            }
//        }