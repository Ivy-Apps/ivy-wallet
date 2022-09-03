package com.ivy.home

import com.ivy.core.action.FlowViewModel
import com.ivy.core.action.calculate.CalculateFlow
import com.ivy.core.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.helper.TrnsListFlow
import com.ivy.core.action.settings.NameFlow
import com.ivy.core.action.settings.balance.HideBalanceSettingFlow
import com.ivy.core.action.time.SelectedPeriodFlow
import com.ivy.core.functions.time.period
import com.ivy.core.functions.transaction.TrnWhere.ActualBetween
import com.ivy.core.functions.transaction.TrnWhere.DueBetween
import com.ivy.core.functions.transaction.or
import com.ivy.core.ui.navigation.Nav
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
) : FlowViewModel<HomeState, HomeState, HomeEvent>() {
    override fun initialState(): HomeState = HomeState(
        name = "",
        period = null,
        trnsList = TransactionsList(
            upcoming = null,
            overdue = null,
            history = emptyList()
        ),
        balance = 0.0,
        income = 0.0,
        expense = 0.0,
        hideBalance = false,
    )

    private val overrideShowBalance = MutableStateFlow(false)

    override fun stateFlow(): Flow<HomeState> = combine(
        nameFlow(Unit), showBalanceFlow(), dataFlow()
    ) { name, hideBalance, data ->
        HomeState(
            name = name,
            period = data.period,
            trnsList = data.trnsList,
            balance = data.balance,
            income = data.income,
            expense = data.expense,
            hideBalance = hideBalance,
        )
    }

    private fun dataFlow(): Flow<DataHolder> = baseCurrencyFlow()
        .flatMapMerge { baseCurrency ->
            val balanceFlow = balanceFlow(
                TotalBalanceFlow.Input(
                    withExcludedAccs = false,
                    outputCurrency = baseCurrency
                )
            )

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
                        outputCurrency = baseCurrency
                    )
                )
            }

            combine(
                balanceFlow, selectedPeriodFlow, trnsListFlow, statsFlow
            ) { balance, selectedPeriod, trnsList, stats ->
                DataHolder(
                    period = selectedPeriod,
                    balance = balance,
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

    private data class DataHolder(
        val period: SelectedPeriod,
        val balance: Double,
        val income: Double,
        val expense: Double,
        val trnsList: TransactionsList,
    )
}

