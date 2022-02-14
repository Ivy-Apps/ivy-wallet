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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.Constants
import com.ivy.wallet.base.horizontalSwipeListener
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.base.verticalSwipeListener
import com.ivy.wallet.logic.model.CustomerJourneyCardData
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.ui.theme.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.transaction.transactions

private const val SWIPE_HORIZONTAL_THRESHOLD = 200

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.HomeTab(screen: Main) {
    val viewModel: HomeViewModel = viewModel()

    val ivyContext = ivyWalletCtx()

    val theme by viewModel.theme.collectAsState()
    val name by viewModel.name.collectAsState()
    val period by viewModel.period.collectAsState()
    val currencyCode by viewModel.baseCurrencyCode.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    val balance by viewModel.balance.collectAsState()
    val buffer by viewModel.buffer.collectAsState()
    val bufferDiff by viewModel.bufferDiff.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()

    val upcomingExpanded by viewModel.upcomingExpanded.collectAsState()
    val upcomingIncome by viewModel.upcomingIncome.collectAsState()
    val upcomingExpenses by viewModel.upcomingExpenses.collectAsState()
    val upcoming by viewModel.upcoming.collectAsState()

    val overdueExpanded by viewModel.overdueExpanded.collectAsState()
    val overdueIncome by viewModel.overdueIncome.collectAsState()
    val overdueExpenses by viewModel.overdueExpenses.collectAsState()
    val overdue by viewModel.overdue.collectAsState()

    val history by viewModel.history.collectAsState()

    //Customer Journey
    val customerJourneyCards by viewModel.customerJourneyCards.collectAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        theme = theme,
        name = name,
        period = period,
        currencyCode = currencyCode,

        categories = categories,
        accounts = accounts,

        balance = balance,
        bufferDiff = bufferDiff,
        buffer = buffer,
        monthlyIncome = monthlyIncome,
        monthlyExpenses = monthlyExpenses,

        upcomingExpanded = upcomingExpanded,
        setUpcomingExpanded = viewModel::setUpcomingExpanded,
        upcomingIncome = upcomingIncome,
        upcomingExpenses = upcomingExpenses,
        upcoming = upcoming,

        overdueExpanded = overdueExpanded,
        setOverdueExpanded = viewModel::setOverdueExpanded,
        overdueIncome = overdueIncome,
        overdueExpenses = overdueExpenses,
        overdue = overdue,

        history = history,

        customerJourneyCards = customerJourneyCards,

        onBalanceClick = viewModel::onBalanceClick,
        onSwitchTheme = viewModel::switchTheme,
        onSetBuffer = viewModel::setBuffer,
        onSetCurrency = viewModel::setCurrency,
        onSetPeriod = viewModel::setPeriod,
        onPayOrGet = viewModel::payOrGet,
        onDismissCustomerJourneyCard = viewModel::dismissCustomerJourneyCard,
        onSelectNextMonth = viewModel::nextMonth,
        onSelectPreviousMonth = viewModel::previousMonth
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    theme: Theme,
    name: String,
    period: TimePeriod,
    currencyCode: String,

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
    onSwitchTheme: () -> Unit = {},
    onSetCurrency: (String) -> Unit = {},
    onSetBuffer: (Double) -> Unit = {},
    onSetPeriod: (TimePeriod) -> Unit = {},
    onPayOrGet: (Transaction) -> Unit = {},
    onDismissCustomerJourneyCard: (CustomerJourneyCardData) -> Unit = {},
    onSelectNextMonth: () -> Unit = {},
    onSelectPreviousMonth: () -> Unit = {},
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

            onShowMonthModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = period
                )
            },
            onBalanceClick = {
                onBalanceClick()
            },
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
            onDismiss = onDismissCustomerJourneyCard
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
        title = "Set currency",
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

    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit,


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
    onDismiss: (CustomerJourneyCardData) -> Unit
) {
    val ivyContext = ivyWalletCtx()
    val nav = navigation()
    val doubleExpanded = remember { mutableStateOf(true) }

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
                if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 && doubleExpanded.value) {
                    moreMenuExpanded(true)
                } else
                    doubleExpanded.value = false

                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (consumed.y <= 30f || available.y >= 1000f)
                    doubleExpanded.value = true
                return super.onPostFling(consumed, available)
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

                monthlyIncome = monthlyIncome,
                monthlyExpenses = monthlyExpenses,

                onOpenMoreMenu = onOpenMoreMenu,
                onBalanceClick = onBalanceClick
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
            emptyStateTitle = "No transactions",
            emptyStateText = "You don't have any transactions for ${
                period.toDisplayLong(ivyContext.startDayOfMonth)
            }.\nYou can add one by tapping the \"+\" button."
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewHomeTab() {
    IvyAppPreview {
        UI(
            theme = Theme.LIGHT,
            name = "Iliyan",
            period = TimePeriod.currentMonth(
                startDayOfMonth = 1
            ), //preview
            currencyCode = "BGN",

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