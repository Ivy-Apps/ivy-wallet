package com.ivy.home

import com.ivy.core.action.FlowViewModel
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.calculate.wallet.TotalBalanceFlow
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.helper.TrnsListFlow
import com.ivy.core.action.time.SelectedPeriodFlow
import com.ivy.core.functions.time.period
import com.ivy.core.functions.transaction.TrnWhere.ActualBetween
import com.ivy.core.functions.transaction.TrnWhere.DueBetween
import com.ivy.core.functions.transaction.or
import com.ivy.core.ui.navigation.Nav
import com.ivy.data.transaction.TransactionsList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(FlowPreview::class)
class HomeViewModel @Inject constructor(
    private val nav: Nav,
    private val balanceFlow: TotalBalanceFlow,
    private val trnsListFlow: TrnsListFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val calculateAct: CalculateAct
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

    override suspend fun stateFlow(): Flow<HomeState> = selectedPeriodFlow()
        .map { selectedPeriod ->
            val period = selectedPeriod.period()
            trnsListFlow(ActualBetween(period) or DueBetween(period))
        }.flattenMerge()
        .map { trnsList ->
            baseCurrencyFlow().map { baseCurrency ->

            }

            TODO()
        }


    override fun mapToUiState(state: StateFlow<HomeState>): StateFlow<HomeState> = state

    override suspend fun handleEvent(event: HomeEvent) = when (event) {
        HomeEvent.BalanceClick -> TODO()
        HomeEvent.HiddenBalanceClick -> TODO()
    }
}