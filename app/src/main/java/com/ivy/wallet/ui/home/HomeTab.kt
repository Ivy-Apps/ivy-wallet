package com.ivy.wallet.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.Theme
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.architecture.FRP
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.ui.theme.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.transaction.transactions
import com.ivy.wallet.utils.horizontalSwipeListener
import com.ivy.wallet.utils.verticalSwipeListener

private const val SWIPE_HORIZONTAL_THRESHOLD = 200

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.HomeTab(screen: Main) {
    FRP<HomeState, HomeEvent, HomeViewModel>(
        initialEvent = HomeEvent.Start
    ) { state, onEvent ->
        UI(
            theme = state.theme,
            name = state.name,
            period = state.period,
            currencyCode = state.baseCurrencyCode,

            hideCurrentBalance = state.hideCurrentBalance,

            categories = state.categories,
            accounts = state.accounts,

            balance = state.balance.toDouble(),
            bufferDiff = state.bufferDiff.toDouble(),
            buffer = state.buffer.toDouble(),
            monthlyIncome = state.monthly.income.toDouble(),
            monthlyExpenses = state.monthly.expense.toDouble(),

            upcomingExpanded = state.upcomingExpanded,
            upcomingIncome = state.upcoming.income.toDouble(),
            upcomingExpenses = state.upcoming.expense.toDouble(),
            upcoming = state.upcomingTrns,

            overdueExpanded = state.overdueExpanded,
            overdueIncome = state.overdue.income.toDouble(),
            overdueExpenses = state.overdue.expense.toDouble(),
            overdue = state.overdueTrns,

            history = state.history,

            customerJourneyCards = state.customerJourneyCards,

            setUpcomingExpanded = { onEvent(HomeEvent.SetUpcomingExpanded(it)) },
            setOverdueExpanded = { onEvent(HomeEvent.SetOverdueExpanded(it)) },
            onBalanceClick = { onEvent(HomeEvent.BalanceClick) },
            onHiddenBalanceClick = { onEvent(HomeEvent.HiddenBalanceClick) },
            onSwitchTheme = { onEvent(HomeEvent.SwitchTheme) },
            onSetBuffer = { onEvent(HomeEvent.SetBuffer(it)) },
            onSetCurrency = { onEvent(HomeEvent.SetCurrency(it)) },
            onSetPeriod = { onEvent(HomeEvent.SetPeriod(it)) },
            onPayOrGet = { onEvent(HomeEvent.PayOrGetPlanned(it)) },
            onSkipTransaction = { onEvent(HomeEvent.SkipPlanned(it)) },
            onDismissCustomerJourneyCard = { onEvent(HomeEvent.DismissCustomerJourneyCard(it)) },
            onSelectNextMonth = { onEvent(HomeEvent.SelectNextMonth) },
            onSelectPreviousMonth = { onEvent(HomeEvent.SelectPreviousMonth) }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    theme: Theme,
    name: String,
    period: TimePeriod,
    currencyCode: String,

    hideCurrentBalance: Boolean,

    categories: List<Category>,
    accounts: List<Account>,

    balance: Double,
    bufferDiff: Double,
    buffer: Double,
    monthlyIncome: Double,
    monthlyExpenses: Double,

    upcomingExpanded: Boolean = true,
    setUpcomingExpanded: (Boolean) -> Unit = {},
    upcomingIncome: Double,
    upcomingExpenses: Double,
    upcoming: List<Transaction>,

    overdueExpanded: Boolean = true,
    setOverdueExpanded: (Boolean) -> Unit = {},
    overdueIncome: Double,
    overdueExpenses: Double,
    overdue: List<Transaction>,

    history: List<TransactionHistoryItem>,

    customerJourneyCards: List<CustomerJourneyCardData> = emptyList(),

    onBalanceClick: () -> Unit = {},
    onHiddenBalanceClick: () -> Unit = {},
    onSwitchTheme: () -> Unit = {},
    onSetCurrency: (String) -> Unit = {},
    onSetBuffer: (Double) -> Unit = {},
    onSetPeriod: (TimePeriod) -> Unit = {},
    onPayOrGet: (Transaction) -> Unit = {},
    onDismissCustomerJourneyCard: (CustomerJourneyCardData) -> Unit = {},
    onSelectNextMonth: () -> Unit = {},
    onSelectPreviousMonth: () -> Unit = {},
    onSkipTransaction: (Transaction) -> Unit = {},
) {
    val ivyContext = ivyWalletCtx()

    var bufferModalData: BufferModalData? by remember { mutableStateOf(null) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }
    var moreMenuExpanded by remember { mutableStateOf(ivyContext.moreMenuExpanded) }
    val setMoreMenuExpanded = { expanded: Boolean ->
        moreMenuExpanded = expanded
        ivyContext.setMoreMenuExpanded(expanded)
    }
    val hideBalanceRowState = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalSwipeListener(
                sensitivity = Constants.SWIPE_DOWN_THRESHOLD_OPEN_MORE_MENU,
                onSwipeDown = {
                    setMoreMenuExpanded(true)
                }
            )
            .horizontalSwipeListener(
                sensitivity = SWIPE_HORIZONTAL_THRESHOLD,
                onSwipeLeft = {
                    ivyContext.selectMainTab(MainTab.ACCOUNTS)
                },
                onSwipeRight = {
                    ivyContext.selectMainTab(MainTab.ACCOUNTS)
                }
            )
    ) {
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = ivyContext.transactionsListState
                ?.firstVisibleItemIndex ?: 0,
            initialFirstVisibleItemScrollOffset = ivyContext.transactionsListState
                ?.firstVisibleItemScrollOffset ?: 0
        )

        HomeHeader(
            expanded = !hideBalanceRowState.value,
            name = name,
            period = period,
            currency = currencyCode,
            balance = balance,
            bufferDiff = bufferDiff,
            hideCurrentBalance = hideCurrentBalance,

            onShowMonthModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = period
                )
            },
            onBalanceClick = {
                onBalanceClick()
            },
            onHiddenBalanceClick = onHiddenBalanceClick,
            onSelectNextMonth = onSelectNextMonth,
            onSelectPreviousMonth = onSelectPreviousMonth
        )

        HomeLazyColumn(
            hideBalanceRowState = hideBalanceRowState,
            currency = currencyCode,
            balance = balance,
            bufferDiff = bufferDiff,
            onOpenMoreMenu = {
                setMoreMenuExpanded(true)
            },
            onBalanceClick = {
                onBalanceClick()
            },
            onHiddenBalanceClick = onHiddenBalanceClick,

            hideCurrentBalance = hideCurrentBalance,


            period = period,
            listState = listState,
            baseCurrency = currencyCode,
            categories = categories,
            accounts = accounts,

            upcomingExpanded = upcomingExpanded,
            setUpcomingExpanded = setUpcomingExpanded,
            upcomingIncome = upcomingIncome,
            upcomingExpenses = upcomingExpenses,
            upcoming = upcoming,

            moreMenuExpanded = setMoreMenuExpanded,

            overdueExpanded = overdueExpanded,
            setOverdueExpanded = setOverdueExpanded,
            overdueIncome = overdueIncome,
            overdueExpenses = overdueExpenses,
            overdue = overdue,

            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,
            history = history,

            customerJourneyCards = customerJourneyCards,

            onPayOrGet = onPayOrGet,
            onDismiss = onDismissCustomerJourneyCard,
            onSkipTransaction = onSkipTransaction
        )
    }

    MoreMenu(
        expanded = moreMenuExpanded,
        theme = theme,
        balance = balance,
        currency = currencyCode,
        buffer = buffer,
        onSwitchTheme = onSwitchTheme,

        setExpanded = setMoreMenuExpanded,
        onBufferClick = {
            bufferModalData = BufferModalData(
                balance = balance,
                currency = currencyCode,
                buffer = buffer
            )
        },
        onCurrencyClick = {
            currencyModalVisible = true
        }
    )

    BufferModal(
        modal = bufferModalData,
        dismiss = {
            bufferModalData = null
        }
    ) {
        onSetBuffer(it)
    }

    CurrencyModal(
        title = stringResource(R.string.set_currency),
        initialCurrency = IvyCurrency.fromCode(currencyCode),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false }
    ) {
        onSetCurrency(it)
    }

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        }
    ) {
        onSetPeriod(it)
    }
}

@ExperimentalAnimationApi
@Composable
fun HomeLazyColumn(
    hideBalanceRowState: MutableState<Boolean>,
    currency: String,
    balance: Double,
    bufferDiff: Double,

    hideCurrentBalance: Boolean,

    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit,
    onHiddenBalanceClick: () -> Unit = {},


    period: TimePeriod,
    listState: LazyListState,

    baseCurrency: String,
    categories: List<Category>,
    accounts: List<Account>,


    upcomingExpanded: Boolean,
    setUpcomingExpanded: (Boolean) -> Unit,
    upcomingIncome: Double,
    upcomingExpenses: Double,
    upcoming: List<Transaction>,

    overdueExpanded: Boolean,
    setOverdueExpanded: (Boolean) -> Unit,
    overdueIncome: Double,
    overdueExpenses: Double,
    overdue: List<Transaction>,

    moreMenuExpanded: (Boolean) -> Unit,

    monthlyIncome: Double,
    monthlyExpenses: Double,

    customerJourneyCards: List<CustomerJourneyCardData>,

    history: List<TransactionHistoryItem>,
    onPayOrGet: (Transaction) -> Unit,
    onDismiss: (CustomerJourneyCardData) -> Unit,
    onSkipTransaction: (Transaction) -> Unit = {},
) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (listState.firstVisibleItemIndex == 0) {
                    //To prevent unnecessary updates
                    if (listState.firstVisibleItemScrollOffset >= 150 && !hideBalanceRowState.value) {
                        hideBalanceRowState.value = true
                    } else if (listState.firstVisibleItemScrollOffset < 150 && hideBalanceRowState.value) {
                        hideBalanceRowState.value = false
                    }
                }

                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        state = listState
    ) {
        item {
            CashFlowInfo(
                period = period,
                currency = currency,
                balance = balance,
                bufferDiff = bufferDiff,

                hideCurrentBalance = hideCurrentBalance,

                monthlyIncome = monthlyIncome,
                monthlyExpenses = monthlyExpenses,

                onOpenMoreMenu = onOpenMoreMenu,
                onBalanceClick = onBalanceClick,
                onHiddenBalanceClick = onHiddenBalanceClick
            )
        }
        item {
            Spacer(Modifier.height(16.dp))

            TransactionsDividerLine()
        }

        item {
            CustomerJourney(
                customerJourneyCards = customerJourneyCards,
                onDismiss = onDismiss
            )
        }

        transactions(
            ivyContext = ivyContext,
            nav = nav,
            upcoming = upcoming,
            upcomingExpanded = upcomingExpanded,
            setUpcomingExpanded = setUpcomingExpanded,
            baseCurrency = baseCurrency,
            upcomingIncome = upcomingIncome,
            upcomingExpenses = upcomingExpenses,
            categories = categories,
            accounts = accounts,
            listState = listState,
            overdue = overdue,
            overdueExpanded = overdueExpanded,
            setOverdueExpanded = setOverdueExpanded,
            overdueIncome = overdueIncome,
            overdueExpenses = overdueExpenses,
            history = history,
            onPayOrGet = onPayOrGet,
            emptyStateTitle = stringRes(R.string.no_transactions),
            emptyStateText = stringRes(
                R.string.no_transactions_description,
                period.toDisplayLong(ivyContext.startDayOfMonth)
            ),
            onSkipTransaction = onSkipTransaction
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewHomeTab() {
    IvyWalletPreview {
        UI(
            theme = Theme.LIGHT,
            name = "Iliyan",
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            currencyCode = "BGN",

            hideCurrentBalance = false,

            categories = emptyList(),
            accounts = emptyList(),

            balance = 1314.578,
            bufferDiff = 2055.0,
            buffer = 5000.0,
            monthlyIncome = 8000.0,
            monthlyExpenses = 6000.0,

            upcomingIncome = 8000.0,
            upcomingExpenses = 4323.0,
            upcoming = emptyList(),

            overdueIncome = 0.0,
            overdueExpenses = 10.0,
            overdue = emptyList(),

            history = emptyList(),
        )
    }
}