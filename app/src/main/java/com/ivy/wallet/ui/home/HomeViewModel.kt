package com.ivy.wallet.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.CustomerJourneyLogic
import com.ivy.wallet.logic.PlannedPaymentsLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CustomerJourneyCardData
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.TransactionDao
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val walletLogic: WalletLogic,
    private val ivyContext: IvyContext,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val customerJourneyLogic: CustomerJourneyLogic
) : ViewModel() {

    private val _theme = MutableLiveData<Theme>()
    val theme = _theme.asLiveData()

    private val _name = MutableLiveData<String>()
    val name = _name.asLiveData()

    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _currencyCode = MutableLiveData<String>()
    val currencyCode = _currencyCode.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    private val _balance = MutableLiveData<Double>()
    val balance = _balance.asLiveData()

    private val _buffer = MutableLiveData<Double>()
    val buffer = _buffer.asLiveData()

    private val _bufferDiff = MutableLiveData<Double>()
    val bufferDiff = _bufferDiff.asLiveData()

    private val _monthlyIncome = MutableLiveData<Double>()
    val monthlyIncome = _monthlyIncome.asLiveData()

    private val _monthlyExpenses = MutableLiveData<Double>()
    val monthlyExpenses = _monthlyExpenses.asLiveData()

    //Upcoming
    private val _upcoming = MutableLiveData<List<Transaction>>()
    val upcoming = _upcoming.asLiveData()

    private val _upcomingIncome = MutableLiveData<Double>()
    val upcomingIncome = _upcomingIncome.asLiveData()

    private val _upcomingExpenses = MutableLiveData<Double>()
    val upcomingExpenses = _upcomingExpenses.asLiveData()

    private val _upcomingExpanded = MutableLiveData(false)
    val upcomingExpanded = _upcomingExpanded.asLiveData()

    //Overdue
    private val _overdue = MutableLiveData<List<Transaction>>()
    val overdue = _overdue.asLiveData()

    private val _overdueIncome = MutableLiveData<Double>()
    val overdueIncome = _overdueIncome.asLiveData()

    private val _overdueExpenses = MutableLiveData<Double>()
    val overdueExpenses = _overdueExpenses.asLiveData()

    private val _overdueExpanded = MutableLiveData(true)
    val overdueExpanded = _overdueExpanded.asLiveData()

    //History
    private val _history = MutableLiveData<List<TransactionHistoryItem>>()
    val history = _history.asLiveData()

    //Customer Journey
    private val _customerJourneyCards = MutableLiveData<List<CustomerJourneyCardData>>()
    val customerJourneyCards = _customerJourneyCards.asLiveData()

    //TODO: Set proper default value for selected period (startDayOfMonth is hardcoded to 1
    fun start(period: TimePeriod = ivyContext.selectedPeriod) {
        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }

            _theme.value = settings.theme
            _name.value = settings.name
            _currencyCode.value = settings.currency

            _categories.value = ioThread { categoryDao.findAll() }!!
            _accounts.value = ioThread { accountDao.findAll() }!!

            _period.value = period
            val timeRange = period.toRange(ivyContext.startDayOfMonth)

            _balance.value = ioThread { walletLogic.calculateBalance() }!!
            _bufferDiff.value = ioThread { walletLogic.calculateBufferDiff() }!!
            _buffer.value = ioThread { settings.bufferAmount }!!

            _monthlyIncome.value = ioThread { walletLogic.calculateIncome(timeRange) }!!
            _monthlyExpenses.value = ioThread {
                walletLogic.calculateExpenses(timeRange)
            }!!

            _upcomingIncome.value = ioThread { walletLogic.calculateUpcomingIncome(timeRange) }!!
            _upcomingExpenses.value =
                ioThread { walletLogic.calculateUpcomingExpenses(timeRange) }!!
            _upcoming.value = ioThread { walletLogic.upcomingTransactions(timeRange) }!!

            _overdueIncome.value = ioThread { walletLogic.calculateOverdueIncome(timeRange) }!!
            _overdueExpenses.value = ioThread { walletLogic.calculateOverdueExpenses(timeRange) }!!
            _overdue.value = ioThread { walletLogic.overdueTransactions(timeRange) }!!

            _history.value = ioThread { walletLogic.history(timeRange) }!!

            _customerJourneyCards.value = ioThread { customerJourneyLogic.loadCards() }!!
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
            val hasTransactions = ioThread { transactionDao.findAll_LIMIT_1().isNotEmpty() }
            if (hasTransactions) {
                //has transactions show him "Balance" screen
                ivyContext.navigateTo(Screen.BalanceScreen)
            } else {
                //doesn't have transactions lead him to adjust balance
                ivyContext.mainTab = MainTab.ACCOUNTS
                ivyContext.navigateTo(Screen.Main)
            }
        }
    }

    fun switchTheme() {
        viewModelScope.launch {
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
        }
    }

    fun setBuffer(newBuffer: Double) {
        viewModelScope.launch {
            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        bufferAmount = newBuffer
                    )
                )
            }
            start()
        }
    }

    fun setCurrency(newCurrency: String) {
        viewModelScope.launch {
            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        currency = newCurrency
                    )
                )

                exchangeRatesLogic.sync(baseCurrency = newCurrency)
            }
            start()
        }
    }

    fun setPeriod(period: TimePeriod) {
        start(period = period)
    }

    fun payOrGet(transaction: Transaction) {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start()
            }
        }
    }

    fun dismissCustomerJourneyCard(card: CustomerJourneyCardData) {
        customerJourneyLogic.dismissCard(card)
        start()
    }
}