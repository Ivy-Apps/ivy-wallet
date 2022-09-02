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
import com.ivy.data.CurrencyCode
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnListItem
import com.ivy.screens.BalanceScreen
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
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

    override suspend fun stateFlow(): Flow<HomeState> = combine(
        nameFlow(Unit), hideBalanceFlow(), dataFlow()
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

    private suspend fun dataFlow(): Flow<DataHolder> =
        combine(selectedPeriodFlow(), baseCurrencyFlow()) { selectedPeriod, baseCurrency ->
            val balanceFlow = balanceFlow(baseCurrency = baseCurrency)

            val period = selectedPeriod.period()
            val trnsListFlow = trnsListFlow(ActualBetween(period) or DueBetween(period))

            val incomeExpenseFlow = trnsListFlow.flatMapMerge { trnsList ->
                incomeExpenseFlow(
                    trns = trnsList.history.map { (it as TrnListItem.Trn).trn },
                    baseCurrency = baseCurrency
                )
            }

            combine(
                balanceFlow, trnsListFlow, incomeExpenseFlow
            ) { balance, trnsList, incomeExpense ->
                DataHolder(
                    period = selectedPeriod,
                    balance = balance,
                    income = incomeExpense.income,
                    expense = incomeExpense.expense,
                    trnsList = trnsList,
                )
            }
        }.flattenMerge()

    private suspend fun balanceFlow(baseCurrency: CurrencyCode): Flow<Double> = balanceFlow(
        TotalBalanceFlow.Input(
            withExcludedAccs = false,
            outputCurrency = baseCurrency
        )
    )

    private suspend fun incomeExpenseFlow(
        trns: List<Transaction>,
        baseCurrency: CurrencyCode
    ): Flow<IncomeExpense> = calculateFlow(
        CalculateFlow.Input(
            trns = trns,
            outputCurrency = baseCurrency
        )
    ).map {
        IncomeExpense(income = it.income, expense = it.expense)
    }

    private suspend fun hideBalanceFlow() =
        combine(
            hideBalanceSettingFlow(Unit),
            overrideShowBalance
        ) { hideBalanceSettings, showBalance ->
            showBalance || hideBalanceSettings
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

    private data class IncomeExpense(
        val income: Double,
        val expense: Double,
    )

    private data class DataHolder(
        val period: SelectedPeriod,
        val balance: Double,
        val income: Double,
        val expense: Double,
        val trnsList: TransactionsList,
    )
}