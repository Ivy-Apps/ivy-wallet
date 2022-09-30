package com.ivy.home

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.domain.action.helper.TrnsListFlow
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.action.settings.balance.HideBalanceSettingFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.transaction.TrnQuery.ActualBetween
import com.ivy.core.domain.action.transaction.TrnQuery.DueBetween
import com.ivy.core.domain.action.transaction.or
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.domain.pure.time.period
import com.ivy.core.ui.action.mapping.MapSelectedPeriodUiAct
import com.ivy.core.ui.action.mapping.MapTransactionListUiAct
import com.ivy.core.ui.data.transaction.TransactionsListUi
import com.ivy.data.Value
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnListItem
import com.ivy.home.event.HomeBottomBarAction
import com.ivy.home.event.HomeEvent
import com.ivy.home.state.HomeState
import com.ivy.home.state.HomeStateUi
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val balanceFlow: TotalBalanceFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val trnsListFlow: TrnsListFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val calculateFlow: CalculateFlow,
    private val hideBalanceSettingFlow: HideBalanceSettingFlow,
    private val mapSelectedPeriodUiAct: MapSelectedPeriodUiAct,
    private val mapTransactionListUiAct: MapTransactionListUiAct,
    private val navigator: Navigator,
) : FlowViewModel<HomeState, HomeStateUi, HomeEvent>() {
    // region Initial state
    override fun initialState(): HomeState = HomeState(
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

    override fun initialUiState() = HomeStateUi(
        period = null,
        trnsList = TransactionsListUi(
            upcoming = null,
            overdue = null,
            history = emptyList(),
        ),
        balance = ValueUi(amount = "0.0", currency = ""),
        income = ValueUi(amount = "0.0", currency = ""),
        expense = ValueUi(amount = "0.0", currency = ""),
        hideBalance = false
    )
    // endregion

    private val overrideShowBalance = MutableStateFlow(false)

    override fun stateFlow(): Flow<HomeState> = combine(
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

    private fun periodDataFlow(): Flow<PeriodData> =
        baseCurrencyFlow().flatMapMerge { baseCurrency ->
            val selectedPeriodFlow = selectedPeriodFlow()

            // Trns History, Upcoming & Overdue
            val trnsListFlow = selectedPeriodFlow.flatMapMerge {
                val period = it.period()
                trnsListFlow(ActualBetween(period) or DueBetween(period))
            }

            // Income & Expense for the period
            val statsFlow = trnsListFlow.flatMapMerge { trnsList ->
                calculateFlow(
                    CalculateFlow.Input(
                        // take only transactions from the history, excluding transfers
                        trns = trnsList.history.mapNotNull { (it as? TrnListItem.Trn)?.trn },
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
        hideBalanceSettingFlow(Unit),
        overrideShowBalance
    ) { hideBalanceSettings, showBalance ->
        showBalance || !hideBalanceSettings
    }

    // region map to Ui state
    override suspend fun mapToUiState(state: HomeState): HomeStateUi = HomeStateUi(
        period = state.period?.let { mapSelectedPeriodUiAct(it) },
        trnsList = mapTransactionListUiAct(state.trnsList),
        balance = formatBalance(state.balance),
        income = format(state.income, shortenFiat = true),
        expense = format(state.expense, shortenFiat = true),
        hideBalance = state.hideBalance
    )

    private fun formatBalance(balance: Value): ValueUi = format(
        value = balance,
        shortenFiat = balance.amount > 10_000
    )
    // endregion

    // region Event Handling
    override suspend fun handleEvent(event: HomeEvent) = when (event) {
        HomeEvent.BalanceClick -> handleBalanceClick()
        HomeEvent.HiddenBalanceClick -> handleHiddenBalanceClick()
        is HomeEvent.BottomBarAction -> handleBottomBarAction(event.action)
        HomeEvent.ExpenseClick -> handleExpenseClick()
        HomeEvent.IncomeClick -> handleIncomeClick()
    }

    private fun handleBottomBarAction(action: HomeBottomBarAction) {
        // TODO: Implement
        navigator.navigate(Destination.debug.route)
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
    // endregion

    private data class PeriodData(
        val period: SelectedPeriod,
        val income: Value,
        val expense: Value,
        val trnsList: TransactionsList,
    )
}