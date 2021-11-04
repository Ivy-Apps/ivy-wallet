package com.ivy.wallet.ui.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.R
import com.ivy.wallet.base.clickableNoIndication
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.statistic.level2.IncomeExpensesCards
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.*
import com.ivy.wallet.ui.theme.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.transaction.transactions

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreen(
    screen: Screen.Report
) {
    val viewModel: ReportViewModel = viewModel()

    val baseCurrency by viewModel.baseCurrency.observeAsState("")
    val balance by viewModel.balance.observeAsState(0.0)
    val income by viewModel.income.observeAsState(0.0)
    val expenses by viewModel.expenses.observeAsState(0.0)
    val upcomingIncome by viewModel.upcomingIncome.observeAsState(0.0)
    val upcomingExpenses by viewModel.upcomingExpenses.observeAsState(0.0)
    val overdueIncome by viewModel.overdueIncome.observeAsState(0.0)
    val overdueExpenses by viewModel.overdueExpenses.observeAsState(0.0)

    val history by viewModel.history.observeAsState(emptyList())
    val upcoming by viewModel.upcoming.observeAsState(emptyList())
    val overdue by viewModel.overdue.observeAsState(emptyList())

    val categories by viewModel.categories.observeAsState(emptyList())
    val accounts by viewModel.accounts.observeAsState(emptyList())

    val upcomingExpanded by viewModel.upcomingExpanded.observeAsState(false)
    val overdueExpanded by viewModel.overdueExpanded.observeAsState(false)

    val filter by viewModel.filter.observeAsState()
    val loading by viewModel.loading.observeAsState(false)

    onScreenStart {
        viewModel.start()
    }

    val context = LocalContext.current
    UI(
        baseCurrency = baseCurrency,
        balance = balance,
        income = income,
        expenses = expenses,
        upcomingIncome = upcomingIncome,
        upcomingExpenses = upcomingExpenses,
        overdueIncome = overdueIncome,
        overdueExpenses = overdueExpenses,
        history = history,
        upcoming = upcoming,
        overdue = overdue,
        categories = categories,
        accounts = accounts,

        upcomingExpanded = upcomingExpanded,
        overdueExpanded = overdueExpanded,

        filter = filter,
        loading = loading,

        setUpcomingExpanded = viewModel::setUpcomingExpanded,
        setOverdueExpanded = viewModel::setOverdueExpanded,
        onPayOrGet = viewModel::payOrGet,
        onSetFilter = viewModel::setFilter,
        onExport = {
            viewModel.export(context)
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    baseCurrency: String,
    balance: Double,

    income: Double,
    expenses: Double,
    upcomingIncome: Double,
    upcomingExpenses: Double,
    overdueIncome: Double,
    overdueExpenses: Double,

    history: List<TransactionHistoryItem>,
    upcoming: List<Transaction>,
    overdue: List<Transaction>,

    categories: List<Category>,
    accounts: List<Account>,

    upcomingExpanded: Boolean,
    overdueExpanded: Boolean,

    filter: ReportFilter?,
    loading: Boolean,

    setUpcomingExpanded: (Boolean) -> Unit = {},
    setOverdueExpanded: (Boolean) -> Unit = {},

    onPayOrGet: (Transaction) -> Unit = {},
    onSetFilter: (ReportFilter?) -> Unit = {},
    onExport: () -> Unit = {},
) {
    val ivyContext = LocalIvyContext.current
    val listState = rememberLazyListState()

    var filterOverlayVisible by remember {
        mutableStateOf(false)
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
                .background(pureBlur())
                .clickableNoIndication {
                    //consume clicks
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Generating report...",
                style = Typo.body1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = Orange
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        stickyHeader {
            Toolbar(
                onExport = onExport,
                onFilter = {
                    filterOverlayVisible = true
                }
            )
        }

        item {
            Text(
                modifier = Modifier.padding(
                    start = 32.dp
                ),
                text = "Reports",
                style = Typo.h2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp),
                textColor = IvyTheme.colors.pureInverse,
                currency = baseCurrency,
                balance = balance,
                balanceAmountPrefix = when {
                    balance > 0 -> "+"
                    else -> null
                }
            )

            Spacer(Modifier.height(20.dp))

            IncomeExpensesCards(
                history = history,
                currency = baseCurrency,
                income = income,
                expenses = expenses,
                hasAddButtons = false,
                itemColor = IvyTheme.colors.pure
            )

            Spacer(Modifier.height(32.dp))

            TransactionsDividerLine(
                paddingHorizontal = 0.dp
            )

            Spacer(Modifier.height(4.dp))
        }

        if (filter != null) {
            transactions(
                ivyContext = ivyContext,
                baseCurrency = baseCurrency,

                upcomingIncome = upcomingIncome,
                upcomingExpenses = upcomingExpenses,
                overdueIncome = overdueIncome,
                overdueExpenses = overdueExpenses,

                categories = categories,
                accounts = accounts,
                listState = listState,

                overdue = overdue,
                overdueExpanded = overdueExpanded,
                setOverdueExpanded = setOverdueExpanded,

                history = history,

                upcoming = upcoming,
                upcomingExpanded = upcomingExpanded,
                setUpcomingExpanded = setUpcomingExpanded,

                lastItemSpacer = 48.dp,
                onPayOrGet = onPayOrGet,
                emptyStateTitle = "No transactions",

                emptyStateText = "You don't have any transactions for your filter."
            )
        } else {
            item {
                NoFilterEmptyState(
                    setFilterOverlayVisible = {
                        filterOverlayVisible = it
                    }
                )
            }
        }

    }


    FilterOverlay(
        visible = filterOverlayVisible,
        baseCurrency = baseCurrency,
        accounts = accounts,
        categories = categories,
        filter = filter,
        onClose = {
            filterOverlayVisible = false
        },
        onSetFilter = onSetFilter
    )
}

@Composable
private fun NoFilterEmptyState(
    setFilterOverlayVisible: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        IvyIcon(
            icon = R.drawable.ic_filter_l,
            tint = Gray
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "No Filter",
            style = Typo.body1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "To generate a report you must first set a valid filter.",
            style = Typo.body2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(32.dp))

        IvyButton(
            iconStart = R.drawable.ic_filter_xs,
            text = "Set Filter"
        ) {
            setFilterOverlayVisible(true)
        }

        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun Toolbar(
    onExport: () -> Unit,
    onFilter: () -> Unit
) {
    val ivyContext = LocalIvyContext.current
    IvyToolbar(
        backButtonType = BackButtonType.CLOSE,
        onBack = {
            ivyContext.back()
        }
    ) {
        Spacer(Modifier.weight(1f))

        //Export CSV
        IvyOutlinedButton(
            text = "Export",
            iconTint = Green,
            textColor = Green,
            solidBackground = true,
            paddingTop = 8.dp,
            paddingBottom = 10.dp,
            iconStart = R.drawable.ic_export_csv
        ) {
            onExport()
        }

        Spacer(Modifier.width(16.dp))

        //Filter
        CircleButtonFilled(
            icon = R.drawable.ic_filter_xs
        ) {
            onFilter()
        }

        Spacer(Modifier.width(24.dp))
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")


        UI(
            baseCurrency = "BGN",
            balance = -6405.66,
            income = 2000.0,
            expenses = 8405.66,
            upcomingIncome = 4800.23,
            upcomingExpenses = 0.0,
            overdueIncome = 2335.12,
            overdueExpenses = 0.0,

            history = emptyList(),
            upcoming = emptyList(),
            overdue = emptyList(),

            upcomingExpanded = true,
            overdueExpanded = true,

            filter = ReportFilter.emptyFilter("BGN"),
            loading = false,

            accounts = listOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = listOf(
                cat1,
                Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                Category("Home", color = Green.toArgb(), icon = null),
            ),
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_NO_FILTER() {
    IvyAppPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")


        UI(
            baseCurrency = "BGN",
            balance = 0.0,
            income = 0.0,
            expenses = 0.0,
            upcomingIncome = 0.0,
            upcomingExpenses = 0.0,
            overdueIncome = 0.0,
            overdueExpenses = 0.0,

            history = emptyList(),
            upcoming = emptyList(),
            overdue = emptyList(),

            upcomingExpanded = true,
            overdueExpanded = true,

            filter = null,
            loading = false,

            accounts = listOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = listOf(
                cat1,
                Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                Category("Home", color = Green.toArgb(), icon = null),
            ),
        )
    }
}