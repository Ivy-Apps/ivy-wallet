package com.ivy.home.old

import com.ivy.base.ClosedTimeRange
import com.ivy.base.MainTab
import com.ivy.base.data.AppBaseData
import com.ivy.base.data.BufferInfo
import com.ivy.base.data.DueSection
import com.ivy.base.toCloseTimeRange
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.AccountOld
import com.ivy.data.Settings
import com.ivy.data.Theme
import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.fixUnit
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.journey.domain.CustomerJourneyCardData
import com.ivy.journey.domain.CustomerJourneyLogic
import com.ivy.screens.BalanceScreen
import com.ivy.screens.Main
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.CalcBufferDiffAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.settings.UpdateSettingsAct
import com.ivy.wallet.domain.action.transaction.HistoryWithDateDivsAct
import com.ivy.wallet.domain.action.viewmodel.home.*
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val nav: Navigation,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic,
    private val historyWithDateDivsAct: HistoryWithDateDivsAct,
    private val calcIncomeExpenseAct: CalcIncomeExpenseAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val settingsAct: SettingsAct,
    private val accountsAct: AccountsActOld,
    private val categoriesAct: CategoriesActOld,
    private val calcBufferDiffAct: CalcBufferDiffAct,
    private val upcomingAct: UpcomingAct,
    private val overdueAct: OverdueAct,
    private val hasTrnsAct: HasTrnsAct,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val shouldHideBalanceAct: ShouldHideBalanceAct,
    private val updateSettingsAct: UpdateSettingsAct,
    private val updateAccCacheAct: UpdateAccCacheAct,
    private val updateCategoriesCacheAct: UpdateCategoriesCacheAct
) : FRPViewModel<HomeStateOld, HomeEventOld>() {
    override val _state: MutableStateFlow<HomeStateOld> = MutableStateFlow(
        HomeStateOld.initial(ivyWalletCtx = ivyContext)
    )

    override suspend fun handleEvent(event: HomeEventOld): suspend () -> HomeStateOld =
        when (event) {
            HomeEventOld.Start -> start()
            HomeEventOld.BalanceClick -> onBalanceClick()
            HomeEventOld.HiddenBalanceClick -> onHiddenBalanceClick()
            is HomeEventOld.PayOrGetPlanned -> payOrGetPlanned(event.transaction)
            is HomeEventOld.SkipPlanned -> skipPlanned(event.transaction)
            is HomeEventOld.SkipAllPlanned -> skipAllPlanned(event.transactions)
            is HomeEventOld.SetPeriod -> setPeriod(event.period)
            HomeEventOld.SelectNextMonth -> nextMonth()
            HomeEventOld.SelectPreviousMonth -> previousMonth()
            is HomeEventOld.SetUpcomingExpanded -> setUpcomingExpanded(event.expanded)
            is HomeEventOld.SetOverdueExpanded -> setOverdueExpanded(event.expanded)
            is HomeEventOld.SetBuffer -> setBuffer(event.buffer).fixUnit()
            is HomeEventOld.SetCurrency -> setCurrency(event.currency).fixUnit()
            HomeEventOld.SwitchTheme -> switchTheme().fixUnit()
            is HomeEventOld.DismissCustomerJourneyCard -> dismissCustomerJourneyCard(event.card)
        }

    private suspend fun start(): suspend () -> HomeStateOld =
        suspend {
            val startDay = startDayOfMonthAct(Unit)
            ivyContext.initSelectedPeriodInMemory(
                startDayOfMonth = startDay
            )
        } then ::reload

    //-----------------------------------------------------------------------------------
    private suspend fun reload(
        period: TimePeriod = ivyContext.selectedPeriod
    ): HomeStateOld = suspend {
        val settings = settingsAct(Unit)
        val hideBalance = shouldHideBalanceAct(Unit)

        updateState {
            it.copy(
                theme = settings.theme,
                name = settings.name,
                period = period,
                hideCurrentBalance = hideBalance
            )
        }

        //This method is used to restore the theme when user imports locally backed up data
        ivyContext.switchTheme(theme = settings.theme)

        Pair(settings, period.toRange(ivyContext.startDayOfMonth).toCloseTimeRange())
    } then ::loadAppBaseData then ::loadIncomeExpenseBalance then
            ::loadBuffer then ::loadTrnHistory then
            ::loadDueTrns thenInvokeAfter ::loadCustomerJourney

    private suspend fun loadAppBaseData(
        input: Pair<Settings, ClosedTimeRange>
    ): Triple<Settings, ClosedTimeRange, List<AccountOld>> =
        suspend {} then accountsAct then updateAccCacheAct then { accounts ->
            accounts
        } then { accounts ->
            val categories = categoriesAct thenInvokeAfter updateCategoriesCacheAct
            accounts to categories
        } thenInvokeAfter { (accounts, categories) ->
            val (settings, timeRange) = input

            updateState {
                it.copy(
                    baseData = AppBaseData(
                        baseCurrency = settings.baseCurrency,
                        categories = categories,
                        accounts = accounts
                    )
                )
            }

            Triple(settings, timeRange, accounts)
        }

    private suspend fun loadIncomeExpenseBalance(
        input: Triple<Settings, ClosedTimeRange, List<AccountOld>>
    ): Triple<Settings, ClosedTimeRange, BigDecimal> {
        val (settings, timeRange, accounts) = input

        val incomeExpense = calcIncomeExpenseAct(
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
                stats = incomeExpense
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
                buffer = BufferInfo(
                    amount = settings.bufferAmount,
                    bufferDiff = calcBufferDiffAct(
                        CalcBufferDiffAct.Input(
                            balance = balance,
                            buffer = settings.bufferAmount
                        )
                    )
                )
            )
        }

        return settings.baseCurrency to timeRange
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

        return baseCurrency to timeRange
    }

    private suspend fun loadDueTrns(
        input: Pair<String, ClosedTimeRange>
    ): HomeStateOld = suspend {
        UpcomingAct.Input(baseCurrency = input.first, range = input.second)
    } then upcomingAct then { result ->
        updateState {
            it.copy(
                upcoming = DueSection(
                    trns = result.upcomingTrns,
                    stats = result.upcoming,
                    expanded = it.upcoming.expanded
                )
            )
        }
    } then {
        OverdueAct.Input(baseCurrency = input.first, toRange = input.second.to)
    } then overdueAct thenInvokeAfter { result ->
        updateState {
            it.copy(
                overdue = DueSection(
                    trns = result.overdueTrns,
                    stats = result.overdue,
                    expanded = it.overdue.expanded
                )
            )
        }
    }

    private suspend fun loadCustomerJourney(
        input: HomeStateOld
    ): HomeStateOld {
        return updateState {
            it.copy(
                customerJourneyCards = ioThread { customerJourneyLogic.loadCards() }
            )
        }
    }
    //-----------------------------------------------------------------

    private suspend fun setUpcomingExpanded(expanded: Boolean) = suspend {
        updateState { it.copy(upcoming = it.upcoming.copy(expanded = expanded)) }
    }

    private suspend fun setOverdueExpanded(expanded: Boolean) = suspend {
        updateState { it.copy(overdue = it.overdue.copy(expanded = expanded)) }
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

    private suspend fun payOrGetPlanned(transaction: TransactionOld) = suspend {
        plannedPaymentsLogic.payOrGet(
            transaction = transaction,
            skipTransaction = false
        ) {
            reload()
        }

        //TODO: Refactor
        stateVal()
    }

    private suspend fun skipPlanned(transaction: TransactionOld) = suspend {
        plannedPaymentsLogic.payOrGet(
            transaction = transaction,
            skipTransaction = true
        ) {
            reload()
        }

        //TODO: Refactor
        stateVal()
    }

    private suspend fun skipAllPlanned(transactions: List<TransactionOld>) = suspend {
        //transactions.forEach {
        //    plannedPaymentsLogic.payOrGet(
        //        transaction = it,
        //        skipTransaction = true
        //    ){
        //        reload()
        //    }
        //}
        plannedPaymentsLogic.payOrGet(
            transactions = transactions,
            skipTransaction = true
        ) {
            reload()
        }
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