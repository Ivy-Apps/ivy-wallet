package com.ivy.wallet.ui.home

import androidx.lifecycle.viewModelScope
import com.ivy.design.l0_system.Theme
import com.ivy.design.navigation.Navigation
import com.ivy.design.viewmodel.IvyViewModel
import com.ivy.wallet.domain.action.wallet.CalcOverdueAct
import com.ivy.wallet.domain.action.wallet.CalcUpcomingAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.action.wallet.HistoryWithDateDivAct
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.domain.fp.wallet.calculateWalletIncomeExpense
import com.ivy.wallet.domain.fp.wallet.walletBufferDiff
import com.ivy.wallet.domain.logic.CustomerJourneyLogic
import com.ivy.wallet.domain.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.logic.model.CustomerJourneyCardData
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.BalanceScreen
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.TestIdlingResource
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
    private val walletDAOs: WalletDAOs,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic,
    private val sharedPrefs: SharedPrefs,
    private val calcWalletBalanceAct: CalcWalletBalanceAct,
    private val calcUpcomingAct: CalcUpcomingAct,
    private val calcOverdueAct: CalcOverdueAct,
    private val historyWithDateDivAct: HistoryWithDateDivAct,
) : IvyViewModel<HomeState>() {
    override val mutableState: MutableStateFlow<HomeState> = MutableStateFlow(
        HomeState.initial(ivyWalletCtx = ivyContext)
    )

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

            val settings = ioThread { settingsDao.findFirst() }

            val hideCurrentBalance = sharedPrefs.getBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, false)

            updateState {
                it.copy(
                    theme = settings.theme,
                    name = settings.name,
                    baseCurrencyCode = settings.currency,
                    period = period,
                    hideCurrentBalance = hideCurrentBalance
                )
            }

            //This method is used to restore the theme when user imports locally backed up data
            loadNewTheme(settings.theme)

            updateState {
                it.copy(
                    categories = ioThread { categoryDao.findAll() },
                    accounts = ioThread { walletDAOs.accountDao.findAll() }
                )
            }

            val timeRange = period.toRange(ivyContext.startDayOfMonth)

            updateState {
                it.copy(
                    balance = calcWalletBalanceAct(settings.currency)
                )
            }

            updateState {
                it.copy(
                    buffer = settings.bufferAmount.toBigDecimal(),
                    bufferDiff = ioThread {
                        walletBufferDiff(
                            settings = settings,
                            balance = stateVal().balance
                        )
                    }
                )
            }

            updateState {
                it.copy(
                    monthly = ioThread {
                        calculateWalletIncomeExpense(
                            walletDAOs = walletDAOs,
                            baseCurrencyCode = stateVal().baseCurrencyCode,
                            range = timeRange.toCloseTimeRange()
                        ).value
                    }
                )
            }

            updateState {
                val result = calcUpcomingAct(timeRange)
                it.copy(
                    upcoming = result.upcoming,
                    upcomingTrns = result.upcomingTrns
                )
            }

            updateState {
                val result = calcOverdueAct(timeRange)
                it.copy(
                    overdue = result.overdue,
                    overdueTrns = result.overdueTrns
                )
            }

            updateState {
                it.copy(
                    history = historyWithDateDivAct(
                        HistoryWithDateDivAct.Input(
                            timeRange = timeRange.toCloseTimeRange(),
                            baseCurrencyCode = stateVal().baseCurrencyCode
                        )
                    )
                )
            }

            updateState {
                it.copy(
                    customerJourneyCards = ioThread { customerJourneyLogic.loadCards() }
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

            val hasTransactions = ioThread {
                walletDAOs.transactionDao.findAll_LIMIT_1().isNotEmpty()
            }
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

    fun payOrGet(transaction: Transaction) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                load()
            }

            TestIdlingResource.decrement()
        }
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