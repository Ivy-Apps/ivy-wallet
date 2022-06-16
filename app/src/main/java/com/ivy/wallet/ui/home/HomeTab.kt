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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.frp.asParamTo2
import com.ivy.frp.forward
import com.ivy.frp.then2
import com.ivy.frp.view.FRP
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.component.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.component.transaction.transactions
import com.ivy.wallet.ui.data.AppBaseData
import com.ivy.wallet.ui.data.BufferInfo
import com.ivy.wallet.ui.data.DueSection
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.utils.horizontalSwipeListener
import com.ivy.wallet.utils.verticalSwipeListener
import java.math.BigDecimal

private const val SWIPE_HORIZONTAL_THRESHOLD = 200

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.HomeTab(screen: Main) {
    FRP<HomeState, HomeEvent, HomeViewModel>(
        initialEvent = HomeEvent.Start
    ) { state, onEvent ->
        UI(state = state, onEvent = onEvent)
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: HomeState,

    onEvent: (HomeEvent) -> Unit
) {
    val ivyContext = ivyWalletCtx()

    var bufferModalData: BufferModalData? by remember { mutableStateOf(null) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var choosePeriodModal: ChoosePeriodModalData? by remember {
        mutableStateOf(null)
    }
    var moreMenuExpanded by remember { mutableStateOf(ivyContext.moreMenuExpanded) }
    var skipAllModalVisible by remember { mutableStateOf(false) }
    val setMoreMenuExpanded = { expanded: Boolean ->
        moreMenuExpanded = expanded
        ivyContext.setMoreMenuExpanded(expanded)
    }
    val hideBalanceRowState = remember { mutableStateOf(false) }

    val baseCurrency = state.baseData.baseCurrency

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
            name = state.name,
            period = state.period,
            currency = baseCurrency,
            balance = state.balance.toDouble(),
            bufferDiff = state.buffer.bufferDiff.toDouble(),
            hideCurrentBalance = state.hideCurrentBalance,

            onShowMonthModal = {
                choosePeriodModal = ChoosePeriodModalData(
                    period = state.period
                )
            },
            onBalanceClick = HomeEvent.BalanceClick asParamTo2 onEvent,
            onHiddenBalanceClick = HomeEvent.HiddenBalanceClick asParamTo2 onEvent,
            onSelectNextMonth = HomeEvent.SelectNextMonth asParamTo2 onEvent,
            onSelectPreviousMonth = HomeEvent.SelectPreviousMonth asParamTo2 onEvent
        )

        HomeLazyColumn(
            hideBalanceRowState = hideBalanceRowState,
            balance = state.balance,
            buffer = state.buffer,
            onOpenMoreMenu = {
                setMoreMenuExpanded(true)
            },
            onBalanceClick = HomeEvent.BalanceClick asParamTo2 onEvent,
            onHiddenBalanceClick = HomeEvent.HiddenBalanceClick asParamTo2 onEvent,

            hideCurrentBalance = state.hideCurrentBalance,


            period = state.period,
            listState = listState,

            baseData = state.baseData,

            upcoming = state.upcoming,
            overdue = state.overdue,

            stats = state.stats,
            history = state.history,

            customerJourneyCards = state.customerJourneyCards,

            onPayOrGet = forward<Transaction>() then2 {
                HomeEvent.PayOrGetPlanned(it)
            } then2 onEvent,
            onDismiss = forward<CustomerJourneyCardData>() then2 {
                HomeEvent.DismissCustomerJourneyCard(it)
            } then2 onEvent,
            onSkipTransaction = forward<Transaction>() then2 {
                HomeEvent.SkipPlanned(it)
            } then2 onEvent,
            setUpcomingExpanded = forward<Boolean>() then2 {
                HomeEvent.SetUpcomingExpanded(it)
            } then2 onEvent,
            setOverdueExpanded = forward<Boolean>() then2 {
                HomeEvent.SetOverdueExpanded(it)
            } then2 onEvent,
            onSkipAllTransactions = { skipAllModalVisible = true }
        )
    }

    MoreMenu(
        expanded = moreMenuExpanded,
        theme = state.theme,
        balance = state.balance.toDouble(),
        currency = baseCurrency,
        buffer = state.buffer.amount.toDouble(),
        onSwitchTheme = HomeEvent.SwitchTheme asParamTo2 onEvent,

        setExpanded = setMoreMenuExpanded,
        onBufferClick = {
            bufferModalData = BufferModalData(
                balance = state.balance.toDouble(),
                currency = baseCurrency,
                buffer = state.buffer.amount.toDouble()
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
        },
        onBufferChanged = forward<Double>() then2 {
            HomeEvent.SetBuffer(it)
        } then2 onEvent
    )

    CurrencyModal(
        title = stringResource(R.string.set_currency),
        initialCurrency = IvyCurrency.fromCode(baseCurrency),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false },
        onSetCurrency = forward<String>() then2 {
            HomeEvent.SetCurrency(it)
        } then2 onEvent
    )

    ChoosePeriodModal(
        modal = choosePeriodModal,
        dismiss = {
            choosePeriodModal = null
        },
        onPeriodSelected = forward<TimePeriod>() then2 {
            HomeEvent.SetPeriod(it)
        } then2 onEvent
    )

    DeleteModal(
        visible = skipAllModalVisible,
        title = stringResource(R.string.confirm_skip_all),
        description = stringResource(R.string.confirm_skip_all_description),
        dismiss = { skipAllModalVisible = false }
    ) {
        onEvent(HomeEvent.SkipAllPlanned(state.overdue.trns))
        skipAllModalVisible = false
    }
}

@ExperimentalAnimationApi
@Composable
fun HomeLazyColumn(
    hideBalanceRowState: MutableState<Boolean>,
    listState: LazyListState,

    buffer: BufferInfo,
    hideCurrentBalance: Boolean,
    period: TimePeriod,

    baseData: AppBaseData,

    upcoming: DueSection,
    overdue: DueSection,
    balance: BigDecimal,
    stats: IncomeExpensePair,
    history: List<TransactionHistoryItem>,


    customerJourneyCards: List<CustomerJourneyCardData>,


    setUpcomingExpanded: (Boolean) -> Unit,
    setOverdueExpanded: (Boolean) -> Unit,

    onOpenMoreMenu: () -> Unit,
    onBalanceClick: () -> Unit,
    onHiddenBalanceClick: () -> Unit = {},

    onPayOrGet: (Transaction) -> Unit,
    onDismiss: (CustomerJourneyCardData) -> Unit,
    onSkipTransaction: (Transaction) -> Unit = {},
    onSkipAllTransactions: (List<Transaction>) -> Unit = {}
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
            .nestedScroll(nestedScrollConnection)
            .testTag("home_lazy_column"),
        state = listState
    ) {
        item {
            CashFlowInfo(
                period = period,
                currency = baseData.baseCurrency,
                balance = balance.toDouble(),
                bufferDiff = buffer.bufferDiff.toDouble(),

                hideCurrentBalance = hideCurrentBalance,

                monthlyIncome = stats.income.toDouble(),
                monthlyExpenses = stats.expense.toDouble(),

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
            baseData = baseData,
            upcoming = upcoming,
            setUpcomingExpanded = setUpcomingExpanded,
            overdue = overdue,
            setOverdueExpanded = setOverdueExpanded,
            history = history,
            onPayOrGet = onPayOrGet,
            emptyStateTitle = stringRes(R.string.no_transactions),
            emptyStateText = stringRes(
                R.string.no_transactions_description,
                period.toDisplayLong(ivyContext.startDayOfMonth)
            ),
            onSkipTransaction = onSkipTransaction,
            onSkipAllTransactions = onSkipAllTransactions
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
            state = HomeState.initial(ivyWalletCtx()),
            onEvent = {}
        )
    }
}