package com.ivy.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.legacy.stringRes
import com.ivy.base.model.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.data.DueSection
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.ui.component.IncomeExpensesCards
import com.ivy.legacy.ui.component.transaction.TransactionsDividerLine
import com.ivy.legacy.ui.component.transaction.transactions
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.ReportScreen
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.GreenDark
import com.ivy.wallet.ui.theme.GreenLight
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Purple1Dark
import com.ivy.wallet.ui.theme.Red3Light
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.CircleButtonFilled
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.pureBlur
import kotlinx.collections.immutable.persistentListOf

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreen(
    screen: ReportScreen
) {
    val viewModel: ReportViewModel = viewModel()
    val state = viewModel.uiState()

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
    val nav = navigation()
    val context = LocalContext.current

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
                .background(pureBlur())
                .clickableNoIndication {
                    // consume clicks
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.generating_report),
                style = UI.typo.b1.style(
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
                onExport = {
                    onEventHandler.invoke(ReportScreenEvent.OnExport(context = context))
                },
                onFilter = {
                    onEventHandler.invoke(
                        ReportScreenEvent.OnFilterOverlayVisible(
                            filterOverlayVisible = true
                        )
                    )
                }
            )
        }

        item {
            Text(
                modifier = Modifier.padding(
                    start = 32.dp
                ),
                text = stringResource(R.string.reports),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            BalanceRow(
                modifier = Modifier
                    .padding(start = 32.dp),
                textColor = UI.colors.pureInverse,
                currency = state.baseCurrency,
                balance = state.balance,
                balanceAmountPrefix = when {
                    state.balance > 0 -> "+"
                    else -> null
                }
            )

            Spacer(Modifier.height(20.dp))

            IncomeExpensesCards(
                history = state.history,
                currency = state.baseCurrency,
                income = state.income,
                expenses = state.expenses,
                hasAddButtons = false,
                itemColor = UI.colors.pure,
                incomeHeaderCardClicked = {
                    if (state.transactions.isNotEmpty()) {
                        nav.navigateTo(
                            PieChartStatisticScreen(
                                type = TransactionType.INCOME,
                                transactions = state.transactions,
                                accountList = state.accountIdFilters,
                                treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                            )
                        )
                    }
                },
                expenseHeaderCardClicked = {
                    if (state.transactions.isNotEmpty()) {
                        nav.navigateTo(
                            PieChartStatisticScreen(
                                type = TransactionType.EXPENSE,
                                transactions = state.transactions,
                                accountList = state.accountIdFilters,
                                treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                            )
                        )
                    }
                }
            )

            if (state.showTransfersAsIncExpCheckbox) {
                IvyCheckboxWithText(
                    modifier = Modifier
                        .padding(16.dp),
                    text = stringResource(R.string.transfers_as_income_expense),
                    checked = state.treatTransfersAsIncExp
                ) {
                    onEventHandler.invoke(
                        ReportScreenEvent.OnTreatTransfersAsIncomeExpense(
                            transfersAsIncomeExpense = it
                        )
                    )
                }
            } else {
                Spacer(Modifier.height(32.dp))
            }

            TransactionsDividerLine(
                paddingHorizontal = 0.dp
            )

            Spacer(Modifier.height(4.dp))
        }

        if (state.filter != null) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = state.baseCurrency,
                    categories = state.categories,
                    accounts = state.accounts,
                ),

                upcoming = DueSection(
                    trns = state.upcomingTransactions,
                    stats = IncomeExpensePair(
                        income = state.upcomingIncome.toBigDecimal(),
                        expense = state.upcomingExpenses.toBigDecimal()
                    ),
                    expanded = state.upcomingExpanded
                ),

                setUpcomingExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnUpcomingExpanded(upcomingExpanded = it))
                },

                overdue = DueSection(
                    trns = state.overdueTransactions,
                    stats = IncomeExpensePair(
                        income = state.overdueIncome.toBigDecimal(),
                        expense = state.overdueExpenses.toBigDecimal()
                    ),
                    expanded = state.overdueExpanded
                ),
                setOverdueExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnOverdueExpanded(overdueExpanded = it))
                },

                history = state.history,
                lastItemSpacer = 48.dp,

                onPayOrGet = {
                    onEventHandler.invoke(ReportScreenEvent.OnPayOrGet(transaction = it))
                },
                emptyStateTitle = stringRes(R.string.no_transactions),
                emptyStateText = stringRes(R.string.no_transactions_for_your_filter),
                onSkipTransaction = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransaction(transaction = it))
                },
                onSkipAllTransactions = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransactions(transactions = it))
                }
            )
        } else {
            item {
                NoFilterEmptyState(
                    setFilterOverlayVisible = {
                        onEventHandler.invoke(
                            ReportScreenEvent.OnFilterOverlayVisible(
                                filterOverlayVisible = it
                            )
                        )
                    }
                )
            }
        }
    }

    FilterOverlay(
        visible = state.filterOverlayVisible,
        baseCurrency = state.baseCurrency,
        accounts = state.accounts,
        categories = state.categories,
        filter = state.filter,
        onClose = {
            onEventHandler.invoke(
                ReportScreenEvent.OnFilterOverlayVisible(
                    filterOverlayVisible = false
                )
            )
        },
        onSetFilter = {
            onEventHandler.invoke(ReportScreenEvent.OnFilter(filter = it))
        }
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
            text = stringResource(R.string.no_filter),
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(R.string.invalid_filter_warning),
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(32.dp))

        IvyButton(
            iconStart = R.drawable.ic_filter_xs,
            text = stringResource(R.string.set_filter)
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
    val nav = navigation()
    IvyToolbar(
        backButtonType = BackButtonType.CLOSE,
        onBack = {
            nav.back()
        }
    ) {
        Spacer(Modifier.weight(1f))

        // Export CSV
        IvyOutlinedButton(
            text = stringResource(R.string.export),
            iconTint = Green,
            textColor = Green,
            solidBackground = true,
            padding = 8.dp,
            iconStart = R.drawable.ic_export_csv
        ) {
            onExport()
        }

        Spacer(Modifier.width(16.dp))

        // Filter
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
    IvyWalletPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = -6405.66,
            income = 2000.0,
            expenses = 8405.66,
            upcomingIncome = 4800.23,
            upcomingExpenses = 0.0,
            overdueIncome = 2335.12,
            overdueExpenses = 0.0,
            history =
            persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),

            upcomingExpanded = true,
            overdueExpanded = true,
            filter = ReportFilter.emptyFilter("BGN"),
            loading = false,
            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                Category("Home", color = Green.toArgb(), icon = null),
            ),
        )

        UI(state = state)
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_NO_FILTER() {
    IvyWalletPreview {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = 0.0,
            income = 0.0,
            expenses = 0.0,
            upcomingIncome = 0.0,
            upcomingExpenses = 0.0,
            overdueIncome = 0.0,
            overdueExpenses = 0.0,

            history = persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),

            upcomingExpanded = true,
            overdueExpanded = true,

            filter = null,
            loading = false,

            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                Category("Home", color = Green.toArgb(), icon = null),
            ),
        )

        UI(state = state)
    }
}
