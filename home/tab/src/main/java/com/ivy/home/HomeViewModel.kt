package com.ivy.home

import com.ivy.common.time.beginningOfIvyTime
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.domain.action.helper.TrnsListFlow
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.action.settings.balance.HideBalanceFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.transaction.*
import com.ivy.core.domain.action.transaction.TrnQuery.*
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.domain.pure.time.range
import com.ivy.core.ui.action.mapping.MapSelectedPeriodUiAct
import com.ivy.core.ui.action.mapping.trn.MapTransactionListUiAct
import com.ivy.core.ui.data.transaction.TransactionsListUi
import com.ivy.data.Value
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnPurpose
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.home.state.HomeState
import com.ivy.home.state.HomeStateUi
import com.ivy.main.base.MainBottomBarAction
import com.ivy.main.base.MainBottomBarVisibility
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.navigation.destinations.transaction.NewTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val balanceFlow: TotalBalanceFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val trnsListFlow: TrnsListFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val calculateFlow: CalculateFlow,
    private val hideBalanceFlow: HideBalanceFlow,
    private val mapSelectedPeriodUiAct: MapSelectedPeriodUiAct,
    private val mapTransactionListUiAct: MapTransactionListUiAct,
    private val navigator: Navigator,
    private val mainBottomBarVisibility: MainBottomBarVisibility,
    private val trnsFlow: TrnsFlow,
) : FlowViewModel<HomeState, HomeStateUi, HomeEvent>() {
    // region Initial state
    override val initialState: HomeState = HomeState(
        period = null,
        trnsList = TransactionsList(
            upcoming = null,
            overdue = null,
            history = emptyList()
        ),
        balance = Value(amount = 0.0, currency = ""),
        income = Value(amount = 0.0, currency = ""),
        expense = Value(amount = 0.0, currency = ""),
        hideBalance = false,
    )

    private val addTransactionModal = IvyModal()

    override val initialUi = HomeStateUi(
        period = null,
        trnsList = TransactionsListUi(
            upcoming = null,
            overdue = null,
            history = emptyList(),
        ),
        balance = ValueUi(amount = "0.0", currency = ""),
        income = ValueUi(amount = "0.0", currency = ""),
        expense = ValueUi(amount = "0.0", currency = ""),
        hideBalance = false,
        moreMenuVisible = false,

        addTransactionModal = addTransactionModal,
    )
    // endregion

    private val overrideShowBalance = MutableStateFlow(false)
    private val moreMenuVisible = MutableStateFlow(initialUi.moreMenuVisible)

    // region State flow
    override val stateFlow: Flow<HomeState> = combine(
        showBalanceFlow(), balanceFlow(), periodDataFlow()
    ) { showBalance, balance, periodData ->
        HomeState(
            period = periodData.period,
            trnsList = periodData.trnsList,
            balance = balance,
            income = periodData.income,
            expense = periodData.expense,
            hideBalance = !showBalance,
        )
    }

    private fun balanceFlow(): Flow<Value> = balanceFlow(
        TotalBalanceFlow.Input(
            withExcludedAccs = false,
        )
    ).onStart {
        // emit initial balance so combine doesn't wait for this long calculation to complete
        emit(Value(amount = 0.0, currency = ""))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun periodDataFlow(): Flow<PeriodData> =
        baseCurrencyFlow().flatMapLatest { baseCurrency ->
            val selectedPeriodFlow = selectedPeriodFlow()

            // Trns History, Upcoming & Overdue
            val trnsListFlow = selectedPeriodFlow.flatMapLatest {
                val period = it.range()
                // Due range: upcoming for this month + overdue for all time
                val dueRange = TimeRange(
                    from = beginningOfIvyTime(),
                    to = period.to,
                )
                trnsListFlow(ActualBetween(period) or DueBetween(dueRange))
            }

            // Income & Expense for the period
            val statsFlow = selectedPeriodFlow.flatMapLatest {
                val period = it.range()

                // take only transactions from the history, excluding transfers
                // but INCLUDING transfer fees
                trnsFlow(
                    ActualBetween(period) and brackets(
                        ByPurpose(null) or ByPurpose(TrnPurpose.Fee)
                    )
                )
            }.flatMapLatest { trns ->
                calculateFlow(
                    CalculateFlow.Input(
                        trns = trns,
                        outputCurrency = baseCurrency,
                        includeTransfers = false,
                        includeHidden = false,
                    )
                )
            }

            combine(
                selectedPeriodFlow, trnsListFlow, statsFlow
            ) { selectedPeriod, trnsList, stats ->
                PeriodData(
                    period = selectedPeriod,
                    income = stats.income,
                    expense = stats.expense,
                    trnsList = trnsList,
                )
            }
        }

    private fun showBalanceFlow(): Flow<Boolean> = combine(
        hideBalanceFlow(Unit),
        overrideShowBalance
    ) { hideBalanceSettings, showBalance ->
        showBalance || !hideBalanceSettings
    }
    // endregion

    // region UI flow
    override val uiFlow: Flow<HomeStateUi> = combine(
        stateFlow, moreMenuVisible
    ) { state, moreMenuVisible ->
        HomeStateUi(
            period = state.period?.let { mapSelectedPeriodUiAct(it) },
            trnsList = mapTransactionListUiAct(state.trnsList),
            balance = formatBalance(state.balance),
            income = format(state.income, shortenFiat = true),
            expense = format(state.expense, shortenFiat = true),
            hideBalance = state.hideBalance,
            moreMenuVisible = moreMenuVisible,

            addTransactionModal = addTransactionModal,
        )
    }

    private fun formatBalance(balance: Value): ValueUi = format(
        value = balance,
        shortenFiat = balance.amount > 10_000
    )
    // endregion

    // region Event Handling
    override suspend fun handleEvent(event: HomeEvent) = when (event) {
        is HomeEvent.BottomBarAction -> handleBottomBarAction(event.action)
        HomeEvent.AddExpense -> handleAddExpense()
        HomeEvent.AddIncome -> handleAddIncome()
        HomeEvent.AddTransfer -> handleAddTransfer()
        HomeEvent.BalanceClick -> handleBalanceClick()
        HomeEvent.HiddenBalanceClick -> handleHiddenBalanceClick()
        HomeEvent.ExpenseClick -> handleExpenseClick()
        HomeEvent.IncomeClick -> handleIncomeClick()
        HomeEvent.ShowBottomBar -> handleShowBottomBar()
        HomeEvent.HideBottomBar -> handleHideBottomBar()
        HomeEvent.MoreClick -> handleMoreClick()
    }

    private fun handleBottomBarAction(action: MainBottomBarAction) {
        addTransactionModal.show()
    }

    private fun handleAddTransfer() {
        navigator.navigate(Destination.newTransfer.destination(Unit))
    }

    private fun handleAddIncome() {
        navigator.navigate(
            Destination.newTransaction.destination(
                NewTransaction.Arg(trnType = TransactionType.Income)
            )
        )
    }

    private fun handleAddExpense() {
        navigator.navigate(
            Destination.newTransaction.destination(
                NewTransaction.Arg(trnType = TransactionType.Expense)
            )
        )
    }

    private fun handleBalanceClick() {
        // TODO: Implement
    }

    private fun handleExpenseClick() {
        // TODO: Implement
    }

    private fun handleIncomeClick() {
        // TODO: Implement
    }

    private suspend fun handleHiddenBalanceClick() {
        overrideShowBalance.value = true
        delay(3_000L)
        overrideShowBalance.value = false
    }

    private fun handleShowBottomBar() {
        mainBottomBarVisibility.visible.value = true
    }

    private fun handleHideBottomBar() {
        mainBottomBarVisibility.visible.value = false
    }

    private fun handleMoreClick() {
        moreMenuVisible.value = !moreMenuVisible.value
    }
    // endregion

    private data class PeriodData(
        val period: SelectedPeriod,
        val income: Value,
        val expense: Value,
        val trnsList: TransactionsList,
    )
}