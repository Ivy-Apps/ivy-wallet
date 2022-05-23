package com.ivy.wallet.ui.home

import com.ivy.design.l0_system.Theme
import com.ivy.frp.fixUnit
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.CalcBufferDiffAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.settings.UpdateSettingsAct
import com.ivy.wallet.domain.action.transaction.HistoryWithDateDivsAct
import com.ivy.wallet.domain.action.viewmodel.home.HasTrnsAct
import com.ivy.wallet.domain.action.viewmodel.home.OverdueAct
import com.ivy.wallet.domain.action.viewmodel.home.ShouldHideBalanceAct
import com.ivy.wallet.domain.action.viewmodel.home.UpcomingAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Settings
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.CustomerJourneyLogic
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.ui.BalanceScreen
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic,
    private val historyWithDateDivsAct: HistoryWithDateDivsAct,
    private val calcIncomeExpenseAct: CalcIncomeExpenseAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val settingsAct: SettingsAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val calcBufferDiffAct: CalcBufferDiffAct,
    private val upcomingAct: UpcomingAct,
    private val overdueAct: OverdueAct,
    private val hasTrnsAct: HasTrnsAct,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val shouldHideBalanceAct: ShouldHideBalanceAct,
    private val updateSettingsAct: UpdateSettingsAct
) : FRPViewModel<HomeState, HomeEvent>() {
    override val _state: MutableStateFlow<HomeState> = MutableStateFlow(
        HomeState.initial(ivyWalletCtx = ivyContext)
    )

    override suspend fun handleEvent(event: HomeEvent): suspend () -> HomeState = when (event) {
        HomeEvent.Start -> start()
        HomeEvent.BalanceClick -> onBalanceClick()
        HomeEvent.HiddenBalanceClick -> onHiddenBalanceClick()
        is HomeEvent.PayOrGetPlanned -> payOrGetPlanned(event.transaction)
        is HomeEvent.SkipPlanned -> skipPlanned(event.transaction)
        is HomeEvent.SetPeriod -> setPeriod(event.period)
        HomeEvent.SelectNextMonth -> nextMonth()
        HomeEvent.SelectPreviousMonth -> previousMonth()
        is HomeEvent.SetUpcomingExpanded -> setUpcomingExpanded(event.expanded)
        is HomeEvent.SetOverdueExpanded -> setOverdueExpanded(event.expanded)
        is HomeEvent.SetBuffer -> setBuffer(event.buffer).fixUnit()
        is HomeEvent.SetCurrency -> setCurrency(event.currency).fixUnit()
        HomeEvent.SwitchTheme -> switchTheme().fixUnit()
        is HomeEvent.DismissCustomerJourneyCard -> dismissCustomerJourneyCard(event.card)
    }

    private suspend fun start(): suspend () -> HomeState =
        suspend {
            val startDay = startDayOfMonthAct(Unit)
            ivyContext.initSelectedPeriodInMemory(
                startDayOfMonth = startDay
            )
        } then ::reload

    //-----------------------------------------------------------------------------------
    private suspend fun reload(
        period: TimePeriod = ivyContext.selectedPeriod
    ): HomeState = suspend {
        val settings = settingsAct(Unit)
        val hideBalance = shouldHideBalanceAct(Unit)

        updateState {
            it.copy(
                theme = settings.theme,
                name = settings.name,
                baseCurrencyCode = settings.baseCurrency,
                period = period,
                hideCurrentBalance = hideBalance
            )
        }

        //This method is used to restore the theme when user imports locally backed up data
        ivyContext.switchTheme(theme = settings.theme)

        Pair(settings, period.toRange(ivyContext.startDayOfMonth).toCloseTimeRange())
    } then ::loadCategoriesAccounts then ::loadIncomeExpenseBalance then
            ::loadBuffer then ::loadTrnHistory then
            ::loadDueTrns thenInvokeAfter ::loadCustomerJourney

    private suspend fun loadCategoriesAccounts(
        input: Pair<Settings, ClosedTimeRange>
    ): Triple<Settings, ClosedTimeRange, List<Account>> {
        val (settings, timeRange) = input

        val accounts = accountsAct(Unit)

        updateState {
            it.copy(
                categories = categoriesAct(Unit),
                accounts = accounts
            )
        }

        return Triple(settings, timeRange, accounts)
    }

    private suspend fun loadIncomeExpenseBalance(
        input: Triple<Settings, ClosedTimeRange, List<Account>>
    ): Triple<Settings, ClosedTimeRange, BigDecimal> {
        val (settings, timeRange, accounts) = input

        val monthlyIncomeExpense = calcIncomeExpenseAct(
            CalcIncomeExpenseAct.Input(
                baseCurrency = settings.baseCurrency,
                accounts = accounts,
                range = timeRange
            )
        )

        val balance = calcWalletBalanceAct(
            CalcWalletBalanceAct.Input(baseCurrency = settings.baseCurrency)
        )

        updateState {
            it.copy(
                balance = balance,
                monthly = monthlyIncomeExpense
            )
        }

        return Triple(settings, timeRange, balance)
    }

    private suspend fun loadBuffer(
        input: Triple<Settings, ClosedTimeRange, BigDecimal>
    ): Pair<String, ClosedTimeRange> {
        val (settings, timeRange, balance) = input

        updateState {
            it.copy(
                buffer = settings.bufferAmount,
                bufferDiff = calcBufferDiffAct(
                    CalcBufferDiffAct.Input(
                        balance = balance,
                        buffer = settings.bufferAmount
                    )
                )
            )
        }

        return Pair(settings.baseCurrency, timeRange)
    }

    private suspend fun loadTrnHistory(
        input: Pair<String, ClosedTimeRange>
    ): Pair<String, ClosedTimeRange> {
        val (baseCurrency, timeRange) = input
        updateState {
            it.copy(
                history = historyWithDateDivsAct(
                    HistoryWithDateDivsAct.Input(
                        range = timeRange,
                        baseCurrency = baseCurrency
                    )
                )
            )
        }

        return Pair(baseCurrency, timeRange)
    }

    private suspend fun loadDueTrns(
        input: Pair<String, ClosedTimeRange>
    ): HomeState {
        val (baseCurrency, timeRange) = input
        updateState {
            val result = upcomingAct(
                UpcomingAct.Input(
                    range = timeRange,
                    baseCurrency = baseCurrency
                )
            )
            it.copy(
                upcoming = result.upcoming,
                upcomingTrns = result.upcomingTrns
            )
        }

        return updateState {
            val result = overdueAct(
                OverdueAct.Input(
                    toRange = timeRange.to,
                    baseCurrency = baseCurrency
                )
            )
            it.copy(
                overdue = result.overdue,
                overdueTrns = result.overdueTrns
            )
        }
    }

    private suspend fun loadCustomerJourney(
        input: HomeState
    ): HomeState {
        return updateState {
            it.copy(
                customerJourneyCards = ioThread { customerJourneyLogic.loadCards() }
            )
        }
    }
    //-----------------------------------------------------------------

    private suspend fun setUpcomingExpanded(expanded: Boolean) = suspend {
        updateState { it.copy(upcomingExpanded = expanded) }
    }

    private suspend fun setOverdueExpanded(expanded: Boolean) = suspend {
        updateState { it.copy(overdueExpanded = expanded) }
    }

    private suspend fun onBalanceClick() = suspend {
        val hasTransactions = hasTrnsAct(Unit)
        if (hasTransactions) {
            //has transactions show him "Balance" screen
            nav.navigateTo(BalanceScreen)
        } else {
            //doesn't have transactions lead him to adjust balance
            ivyContext.selectMainTab(MainTab.ACCOUNTS)
            nav.navigateTo(Main)
        }

        stateVal()
    }

    private suspend fun onHiddenBalanceClick() = suspend {
        updateState { it.copy(hideCurrentBalance = false) }

        //Showing Balance fow 5s
        delay(5000)

        updateState { it.copy(hideCurrentBalance = true) }
    }

    private suspend fun switchTheme() = settingsAct then {
        it.copy(
            theme = when (it.theme) {
                Theme.LIGHT -> Theme.DARK
                Theme.DARK -> Theme.AUTO
                Theme.AUTO -> Theme.LIGHT
            }
        )
    } then updateSettingsAct then { newSettings ->
        ivyContext.switchTheme(newSettings.theme)
        updateState { it.copy(theme = newSettings.theme) }
    }

    private suspend fun setBuffer(newBuffer: Double) = settingsAct then {
        it.copy(
            bufferAmount = newBuffer.toBigDecimal()
        )
    } then updateSettingsAct then {
        reload()
    }

    private suspend fun setCurrency(newCurrency: String) = settingsAct then {
        it.copy(
            baseCurrency = newCurrency
        )
    } then updateSettingsAct then {
        //update exchange rates from POV of the new base currency
        exchangeRatesLogic.sync(baseCurrency = newCurrency)
    } then {
        reload()
    }

    private suspend fun payOrGetPlanned(transaction: Transaction) = suspend {
        plannedPaymentsLogic.payOrGet(
            transaction = transaction,
            skipTransaction = false
        ) {
            reload()
        }

        //TODO: Refactor
        stateVal()
    }

    private suspend fun skipPlanned(transaction: Transaction) = suspend {
        plannedPaymentsLogic.payOrGet(
            transaction = transaction,
            skipTransaction = true
        ) {
            reload()
        }

        //TODO: Refactor
        stateVal()
    }

    private suspend fun dismissCustomerJourneyCard(card: CustomerJourneyCardData) = suspend {
        customerJourneyLogic.dismissCard(card)
    } then {
        reload()
    }

    private suspend fun nextMonth() = suspend {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        month?.incrementMonthPeriod(ivyContext, 1L, year = year)
    } then {
        if (it != null) {
            reload(it)
        } else stateVal()
    }

    private suspend fun previousMonth() = suspend {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        month?.incrementMonthPeriod(ivyContext, -1L, year = year)
    } then {
        if (it != null) {
            reload(it)
        } else stateVal()
    }

    private suspend fun setPeriod(period: TimePeriod) = suspend {
        reload(period)
    }
}