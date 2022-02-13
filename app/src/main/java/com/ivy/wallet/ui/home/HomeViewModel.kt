package com.ivy.wallet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.asFlow
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.functional.wallet.calculateWalletBalance
import com.ivy.wallet.functional.wallet.calculateWalletIncomeExpense
import com.ivy.wallet.functional.wallet.walletBufferDiff
import com.ivy.wallet.logic.CustomerJourneyLogic
import com.ivy.wallet.logic.PlannedPaymentsLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CustomerJourneyCardData
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val walletLogic: WalletLogic,
    private val ivyContext: IvyContext,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _theme = MutableStateFlow(Theme.LIGHT)
    val theme = _theme.asFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asFlow()

    private val _period = MutableStateFlow(ivyContext.selectedPeriod)
    val period = _period.asFlow()

    private val _baseCurrencyCode = MutableStateFlow("")
    val baseCurrencyCode = _baseCurrencyCode.asFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance = _balance.asFlow()

    private val _buffer = MutableStateFlow(0.0)
    val buffer = _buffer.asFlow()

    private val _bufferDiff = MutableStateFlow(0.0)
    val bufferDiff = _bufferDiff.asFlow()

    private val _monthlyIncome = MutableStateFlow(0.0)
    val monthlyIncome = _monthlyIncome.asFlow()

    private val _monthlyExpenses = MutableStateFlow(0.0)
    val monthlyExpenses = _monthlyExpenses.asFlow()

    //Upcoming
    private val _upcoming = MutableStateFlow<List<Transaction>>(emptyList())
    val upcoming = _upcoming.asFlow()

    private val _upcomingIncome = MutableStateFlow(0.0)
    val upcomingIncome = _upcomingIncome.asFlow()

    private val _upcomingExpenses = MutableStateFlow(0.0)
    val upcomingExpenses = _upcomingExpenses.asFlow()

    private val _upcomingExpanded = MutableStateFlow(false)
    val upcomingExpanded = _upcomingExpanded.asFlow()

    //Overdue
    private val _overdue = MutableStateFlow<List<Transaction>>(emptyList())
    val overdue = _overdue.asFlow()

    private val _overdueIncome = MutableStateFlow<Double>(0.0)
    val overdueIncome = _overdueIncome.asFlow()

    private val _overdueExpenses = MutableStateFlow<Double>(0.0)
    val overdueExpenses = _overdueExpenses.asFlow()

    private val _overdueExpanded = MutableStateFlow(true)
    val overdueExpanded = _overdueExpanded.asFlow()

    //History
    private val _history = MutableStateFlow<List<TransactionHistoryItem>>(emptyList())
    val history = _history.asFlow()

    //Customer Journey
    private val _customerJourneyCards = MutableStateFlow<List<CustomerJourneyCardData>>(emptyList())
    val customerJourneyCards = _customerJourneyCards.asFlow()

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

            _theme.value = settings.theme
            _name.value = settings.name
            _baseCurrencyCode.value = settings.currency

            _categories.value = ioThread { categoryDao.findAll() }!!
            _accounts.value = ioThread { accountDao.findAll() }!!

            _period.value = period
            val timeRange = period.toRange(ivyContext.startDayOfMonth)

            _balance.value = ioThread {
                calculateWalletBalance(
                    accountDao = accountDao,
                    transactionDao = transactionDao,
                    exchangeRateDao = exchangeRateDao,
                    baseCurrencyCode = settings.currency
                ).value.toDouble()
            }

            _buffer.value = settings.bufferAmount
            _bufferDiff.value = ioThread {
                walletBufferDiff(
                    settings = settings,
                    balance = balance.value.toBigDecimal()
                ).toDouble()
            }


            val incomeExpense = ioThread {
                calculateWalletIncomeExpense(
                    accountDao = accountDao,
                    transactionDao = transactionDao,
                    exchangeRateDao = exchangeRateDao,
                    baseCurrencyCode = baseCurrencyCode.value,
                    range = timeRange.toCloseTimeRange()
                ).value
            }
            _monthlyIncome.value = incomeExpense.income.toDouble()
            _monthlyExpenses.value = incomeExpense.expense.toDouble()

            _upcomingIncome.value = ioThread { walletLogic.calculateUpcomingIncome(timeRange) }!!
            _upcomingExpenses.value =
                ioThread { walletLogic.calculateUpcomingExpenses(timeRange) }!!
            _upcoming.value = ioThread { walletLogic.upcomingTransactions(timeRange) }!!

            _overdueIncome.value = ioThread { walletLogic.calculateOverdueIncome(timeRange) }!!
            _overdueExpenses.value = ioThread { walletLogic.calculateOverdueExpenses(timeRange) }!!
            _overdue.value = ioThread { walletLogic.overdueTransactions(timeRange) }!!

            _history.value = ioThread { walletLogic.history(timeRange) }!!

            _customerJourneyCards.value = ioThread { customerJourneyLogic.loadCards() }!!

            TestIdlingResource.decrement()
        }
    }

    fun setUpcomingExpanded(expanded: Boolean) {
        _upcomingExpanded.value = expanded
    }

    fun setOverdueExpanded(expanded: Boolean) {
        _overdueExpanded.value = expanded
    }

    fun onBalanceClick() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val hasTransactions = ioThread { transactionDao.findAll_LIMIT_1().isNotEmpty() }
            if (hasTransactions) {
                //has transactions show him "Balance" screen
                ivyContext.navigateTo(Screen.BalanceScreen)
            } else {
                //doesn't have transactions lead him to adjust balance
                ivyContext.selectMainTab(MainTab.ACCOUNTS)
                ivyContext.navigateTo(Screen.Main)
            }

            TestIdlingResource.decrement()
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
                        Theme.DARK -> Theme.LIGHT
                    }
                )
                settingsDao.save(newSettings)
                newSettings
            }

            ivyContext.switchTheme(newSettings.theme)
            _theme.value = newSettings.theme

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
        val month = period.value?.month
        val year = period.value?.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year = year),
            )
        }
    }

    fun previousMonth() {
        val month = period.value?.month
        val year = period.value?.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year = year),
            )
        }
    }
}