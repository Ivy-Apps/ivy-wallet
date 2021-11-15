package com.ivy.wallet.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
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
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.Theme
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.ui.theme.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.transaction.transactions

private const val SWIPE_DOWN_THRESHOLD_OPEN_MORE_MENU = 200

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.HomeTab(screen: Screen.Main) {
    val viewModel: HomeViewModel = viewModel()

    val ivyContext = LocalIvyContext.current

    val theme by viewModel.theme.observeAsState(Theme.LIGHT)
    val name by viewModel.name.observeAsState("")
    val period by viewModel.period.observeAsState(ivyContext.selectedPeriod)
    val currencyCode by viewModel.currencyCode.observeAsState("")

    val categories by viewModel.categories.observeAsState(emptyList())
    val accounts by viewModel.accounts.observeAsState(emptyList())

    val balance by viewModel.balance.observeAsState(0.0)
    val buffer by viewModel.buffer.observeAsState(0.0)
    val bufferDiff by viewModel.bufferDiff.observeAsState(0.0)
    val monthlyIncome by viewModel.monthlyIncome.observeAsState(0.0)
    val monthlyExpenses by viewModel.monthlyExpenses.observeAsState(0.0)

    val upcomingExpanded by viewModel.upcomingExpanded.observeAsState(true)
    val upcomingIncome by viewModel.upcomingIncome.observeAsState(0.0)
    val upcomingExpenses by viewModel.upcomingExpenses.observeAsState(0.0)
    val upcoming by viewModel.upcoming.observeAsState(emptyList())

    val overdueExpanded by viewModel.overdueExpanded.observeAsState(true)
    val overdueIncome by viewModel.overdueIncome.observeAsState(0.0)
    val overdueExpenses by viewModel.overdueExpenses.observeAsState(0.0)
    val overdue by viewModel.overdue.observeAsState(emptyList())

    val history by viewModel.history.observeAsState(emptyList())

    //Customer Journey
    val customerJourneyCards by viewModel.customerJourneyCards.observeAsState(emptyList())

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
    var bufferModalData: BufferModalData? by remember { mutableStateOf(null) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }
    var expanded by remember { mutableStateOf(false) }

    val ivyContext = LocalIvyContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalSwipeListener(
                sensitivity = SWIPE_DOWN_THRESHOLD_OPEN_MORE_MENU,
                onSwipeDown = {
                    expanded = true
                }
            )
            .horizontalSwipeListener(
                sensitivity = 250,
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
            expanded = listState.firstVisibleItemIndex == 0,
            name = name,
            period = period,
            currency = currencyCode,
            balance = balance,
            bufferDiff = bufferDiff,
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,

            onShowMonthModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = period
                )
            },
            onOpenMoreMenu = {
                expanded = true
            },
            onBalanceClick = {
                onBalanceClick()
            },
            onSelectNextMonth = onSelectNextMonth,
            onSelectPreviousMonth = onSelectPreviousMonth
        )

        HomeTransactionsLazyColumn(
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
        expanded = expanded,
        theme = theme,
        balance = balance,
        currency = currencyCode,
        buffer = buffer,
        onSwitchTheme = onSwitchTheme,

        setExpanded = {
            expanded = it
        },
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

@Composable
fun HomeTransactionsLazyColumn(
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

    monthlyIncome: Double,
    monthlyExpenses: Double,

    customerJourneyCards: List<CustomerJourneyCardData>,

    history: List<TransactionHistoryItem>,
    onPayOrGet: (Transaction) -> Unit,
    onDismiss: (CustomerJourneyCardData) -> Unit
) {
    val ivyContext = LocalIvyContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState
    ) {
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