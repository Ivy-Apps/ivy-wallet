package com.ivy.home

import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.domain.action.helper.TrnsListFlow
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.action.settings.balance.HideBalanceSettingFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.name.NameFlow
import com.ivy.core.domain.action.transaction.TrnQuery.ActualBetween
import com.ivy.core.domain.action.transaction.TrnQuery.DueBetween
import com.ivy.core.domain.action.transaction.or
import com.ivy.core.domain.pure.time.period
import com.ivy.core.ui.navigation.Nav
import com.ivy.data.Value
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnListItem
import com.ivy.screens.BalanceScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val nav: Nav,
    private val balanceFlow: TotalBalanceFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val trnsListFlow: TrnsListFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val calculateFlow: CalculateFlow,
    private val nameFlow: NameFlow,
    private val hideBalanceSettingFlow: HideBalanceSettingFlow,
) : com.ivy.core.domain.action.FlowViewModel<HomeState, HomeState, HomeEvent>() {
    override fun initialState(): HomeState = HomeState(
        name = "",
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

    private val overrideShowBalance = MutableStateFlow(false)

    override fun stateFlow(): Flow<HomeState> = combine(
        nameFlow(Unit), showBalanceFlow(), balanceFlow(), periodDataFlow()
    ) { name, showBalance, balance, periodData ->
        HomeState(
            name = name,
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

    private fun showBalanceFlow() =
        combine(
            hideBalanceSettingFlow(Unit),
            overrideShowBalance
        ) { hideBalanceSettings, showBalance ->
            showBalance || !hideBalanceSettings
        }

    override fun mapToUiState(state: StateFlow<HomeState>): StateFlow<HomeState> = state

    override suspend fun handleEvent(event: HomeEvent) = when (event) {
        HomeEvent.BalanceClick -> handleBalanceClick()
        HomeEvent.HiddenBalanceClick -> handleHiddenBalanceClick()
    }

    private fun handleBalanceClick() {
        nav.navigateTo(BalanceScreen)
    }

    private suspend fun handleHiddenBalanceClick() {
        overrideShowBalance.value = true
        delay(3_000L)
        overrideShowBalance.value = false
    }

    private data class PeriodData(
        val period: SelectedPeriod,
        val income: Value,
        val expense: Value,
        val trnsList: TransactionsList,
    )
}

