package com.ivy.wallet.ui.home

import androidx.lifecycle.viewModelScope
import com.ivy.design.l0_system.Theme
import com.ivy.design.navigation.Navigation
import com.ivy.fp.test.TestIdlingResource
import com.ivy.fp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.CalcBufferDiffAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.transaction.HistoryWithDateDivsAct
import com.ivy.wallet.domain.action.viewmodel.home.HasTrnsAct
import com.ivy.wallet.domain.action.viewmodel.home.OverdueAct
import com.ivy.wallet.domain.action.viewmodel.home.UpcomingAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.CustomerJourneyLogic
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.BalanceScreen
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic,
    private val sharedPrefs: SharedPrefs,
    private val historyWithDateDivsAct: HistoryWithDateDivsAct,
    private val calcIncomeExpenseAct: CalcIncomeExpenseAct,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val settingsAct: SettingsAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val calcBufferDiffAct: CalcBufferDiffAct,
    private val upcomingAct: UpcomingAct,
    private val overdueAct: OverdueAct,
    private val hasTrnsAct: HasTrnsAct
) : FRPViewModel<HomeState, Unit>() {
    override val _state: MutableStateFlow<HomeState> = MutableStateFlow(
        HomeState.initial(ivyWalletCtx = ivyContext)
    )

    override suspend fun handleEvent(event: Unit): suspend () -> HomeState {
        TODO("Not yet implemented")
    }

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val startDayOfMonth = ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            load(
                period = ivyContext.initSelectedPeriodInMemory(
                    startDayOfMonth = startDayOfMonth
                )
            )

            TestIdlingResource.decrement()
        }
    }

    private fun load(period: TimePeriod = ivyContext.selectedPeriod) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val settings = settingsAct(Unit)
            val baseCurrency = settings.baseCurrency

            val hideCurrentBalance = sharedPrefs.getBoolean(
                SharedPrefs.HIDE_CURRENT_BALANCE,
                false
            )

            updateState {
                it.copy(
                    theme = settings.theme,
                    name = settings.name,
                    baseCurrencyCode = baseCurrency,
                    period = period,
                    hideCurrentBalance = hideCurrentBalance
                )
            }

            //This method is used to restore the theme when user imports locally backed up data
            loadNewTheme(settings.theme)

            val accounts = accountsAct(Unit)

            updateState {
                it.copy(
                    categories = categoriesAct(Unit),
                    accounts = accounts
                )
            }

            val timeRange = period.toRange(ivyContext.startDayOfMonth)
                .toCloseTimeRange()

            val monthlyIncomeExpense = calcIncomeExpenseAct(
                CalcIncomeExpenseAct.Input(
                    baseCurrency = baseCurrency,
                    accounts = accounts,
                    range = timeRange
                )
            )

            val balance = calcWalletBalanceAct(
                CalcWalletBalanceAct.Input(baseCurrency = baseCurrency)
            )

            updateState {
                it.copy(
                    balance = balance,
                    monthly = monthlyIncomeExpense
                )
            }

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

            updateState {
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

            updateState {
                it.copy(
                    customerJourneyCards = ioThread { customerJourneyLogic.loadCards() }
                )
            }

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

            TestIdlingResource.decrement()
        }
    }

    private fun loadNewTheme(theme: Theme) {
        ivyContext.switchTheme(theme = theme)
    }

    fun setUpcomingExpanded(expanded: Boolean) {
        updateStateNonBlocking { it.copy(upcomingExpanded = expanded) }
    }

    fun setOverdueExpanded(expanded: Boolean) {
        updateStateNonBlocking { it.copy(overdueExpanded = expanded) }
    }

    fun onBalanceClick() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val hasTransactions = hasTrnsAct(Unit)
            if (hasTransactions) {
                //has transactions show him "Balance" screen
                nav.navigateTo(BalanceScreen)
            } else {
                //doesn't have transactions lead him to adjust balance
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
                nav.navigateTo(Main)
            }

            TestIdlingResource.decrement()
        }
    }

    fun onHiddenBalanceClick() {
        viewModelScope.launch(Dispatchers.Default) {
            updateState {
                it.copy(hideCurrentBalance = false)
            }
            //Showing Balance fow 5s
            delay(5000)
            updateState {
                it.copy(hideCurrentBalance = true)
            }
        }
    }

    fun switchTheme() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val newSettings = ioThread {
                val currentSettings = settingsDao.findFirst()
                val newSettings = currentSettings.copy(
                    theme = when (currentSettings.theme) {
                        Theme.LIGHT -> Theme.DARK
                        Theme.DARK -> Theme.AUTO
                        Theme.AUTO -> Theme.LIGHT
                    }
                )
                settingsDao.save(newSettings)
                newSettings
            }

            ivyContext.switchTheme(newSettings.theme)
            updateState {
                it.copy(
                    theme = newSettings.theme
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun setBuffer(newBuffer: Double) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        bufferAmount = newBuffer
                    )
                )
            }
            load()

            TestIdlingResource.decrement()
        }
    }

    fun setCurrency(newCurrency: String) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        currency = newCurrency
                    )
                )

                exchangeRatesLogic.sync(baseCurrency = newCurrency)
            }
            load()

            TestIdlingResource.decrement()
        }
    }

    fun setPeriod(period: TimePeriod) {
        load(period = period)
    }

    fun payOrGet(transaction: Transaction, skipTransaction: Boolean = false) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(
                transaction = transaction,
                skipTransaction = skipTransaction
            ) {
                load()
            }

            TestIdlingResource.decrement()
        }
    }

    fun skipTransaction(transaction: Transaction) {
        payOrGet(transaction, true)
    }

    fun dismissCustomerJourneyCard(card: CustomerJourneyCardData) {
        customerJourneyLogic.dismissCard(card)
        load()
    }

    fun nextMonth() {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year = year),
            )
        }
    }

    fun previousMonth() {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year = year),
            )
        }
    }
}