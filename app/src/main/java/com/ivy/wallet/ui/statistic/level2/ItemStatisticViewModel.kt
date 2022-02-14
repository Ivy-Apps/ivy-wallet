package com.ivy.wallet.ui.statistic.level2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.design.navigation.Navigation
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.*
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.uploader.AccountUploader
import com.ivy.wallet.sync.uploader.CategoryUploader
import com.ivy.wallet.ui.ItemStatistic
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemStatisticViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val categoryUploader: CategoryUploader,
    private val accountUploader: AccountUploader,
    private val accountLogic: WalletAccountLogic,
    private val categoryLogic: WalletCategoryLogic,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val exchangeRatesLogic: ExchangeRatesLogic
) : ViewModel() {

    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    private val _baseCurrency = MutableLiveData<String>()
    val baseCurrency = _baseCurrency.asLiveData()

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _balance = MutableLiveData<Double>()
    val balance = _balance.asLiveData()

    private val _balanceBaseCurrency = MutableLiveData<Double?>()
    val balanceBaseCurrency = _balanceBaseCurrency.asLiveData()

    private val _income = MutableLiveData<Double>()
    val income = _income.asLiveData()

    private val _expenses = MutableLiveData<Double>()
    val expenses = _expenses.asLiveData()

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

    private val _account = MutableLiveData<Account?>()
    val account = _account.asLiveData()

    private val _category = MutableLiveData<Category?>()
    val category = _category.asLiveData()

    fun start(
        screen: ItemStatistic,
        period: TimePeriod? = ivyContext.selectedPeriod,
        reset: Boolean = true
    ) {
        TestIdlingResource.increment()

        if (reset) {
            reset()
        }

        viewModelScope.launch {
            _period.value = period ?: ivyContext.selectedPeriod


            val baseCurrency = ioThread { settingsDao.findFirst().currency }
            _baseCurrency.value = baseCurrency
            _currency.value = baseCurrency

            _categories.value = ioThread { categoryDao.findAll() }!!
            _accounts.value = ioThread { accountDao.findAll() }!!

            when {
                screen.accountId != null -> {
                    initForAccount(screen.accountId)
                }
                screen.categoryId != null -> {
                    initForCategory(screen.categoryId)
                }
                screen.unspecifiedCategory == true -> {
                    initForUnspecifiedCategory()
                }
                else -> error("no id provided")
            }
        }

        TestIdlingResource.decrement()
    }

    private suspend fun initForAccount(accountId: UUID) {
        val account = ioThread {
            accountDao.findById(accountId) ?: error("account not found")
        }
        _account.value = account
        val range = period.value!!.toRange(ivyContext.startDayOfMonth)

        if (account.currency.isNotNullOrBlank()) {
            _currency.value = account.currency!!
        }

        val balance = ioThread { accountLogic.calculateAccountBalance(account) }
        _balance.value = balance
        if (baseCurrency.value != currency.value) {
            _balanceBaseCurrency.value = ioThread {
                exchangeRatesLogic.amountBaseCurrency(
                    amount = balance,
                    amountCurrency = currency.value ?: "",
                    baseCurrency = baseCurrency.value ?: ""
                )
            }
        }

        _income.value = ioThread {
            accountLogic.calculateAccountIncome(account, range)
        }!!

        _expenses.value = ioThread {
            accountLogic.calculateAccountExpenses(account, range)
        }!!

        _history.value = ioThread {
            accountLogic.historyForAccount(account, range)
        }!!

        //Upcoming
        _upcomingIncome.value = ioThread {
            accountLogic.calculateUpcomingIncome(account, range)
        }!!

        _upcomingExpenses.value = ioThread {
            accountLogic.calculateUpcomingExpenses(account, range)
        }!!

        _upcoming.value = ioThread { accountLogic.upcoming(account, range) }!!

        //Overdue
        _overdueIncome.value = ioThread {
            accountLogic.calculateOverdueIncome(account, range)
        }!!

        _overdueExpenses.value = ioThread {
            accountLogic.calculateOverdueExpenses(account, range)
        }!!

        _overdue.value = ioThread { accountLogic.overdue(account, range) }!!
    }

    private suspend fun initForCategory(categoryId: UUID) {
        val category = ioThread {
            categoryDao.findById(categoryId) ?: error("category not found")
        }
        _category.value = category
        val range = period.value!!.toRange(ivyContext.startDayOfMonth)

        _balance.value = ioThread {
            categoryLogic.calculateCategoryBalance(category, range)
        }!!

        _income.value = ioThread {
            categoryLogic.calculateCategoryIncome(category, range)
        }!!

        _expenses.value = ioThread {
            categoryLogic.calculateCategoryExpenses(category, range)
        }!!

        _history.value = ioThread {
            categoryLogic.historyByCategoryWithDateDividers(category, range)
        }!!

        //Upcoming
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeByCategory(category, range)
        }!!

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesByCategory(category, range)
        }!!

        _upcoming.value = ioThread { categoryLogic.upcomingByCategory(category, range) }!!

        //Overdue
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeByCategory(category, range)
        }!!

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesByCategory(category, range)
        }!!

        _overdue.value = ioThread { categoryLogic.overdueByCategory(category, range) }!!
    }

    private suspend fun initForUnspecifiedCategory() {
        val range = period.value!!.toRange(ivyContext.startDayOfMonth)

        _balance.value = ioThread {
            categoryLogic.calculateUnspecifiedBalance(range)
        }!!

        _income.value = ioThread {
            categoryLogic.calculateUnspecifiedIncome(range)
        }!!

        _expenses.value = ioThread {
            categoryLogic.calculateUnspecifiedExpenses(range)
        }!!

        _history.value = ioThread {
            categoryLogic.historyUnspecified(range)
        }!!

        //Upcoming
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeUnspecified(range)
        }!!

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesUnspecified(range)
        }!!

        _upcoming.value = ioThread { categoryLogic.upcomingUnspecified(range) }!!

        //Overdue
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeUnspecified(range)
        }!!

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesUnspecified(range)
        }!!

        _overdue.value = ioThread { categoryLogic.overdueUnspecified(range) }!!
    }

    private fun reset() {
        _account.value = null
        _category.value = null
    }

    fun setUpcomingExpanded(expanded: Boolean) {
        _upcomingExpanded.value = expanded
    }

    fun setOverdueExpanded(expanded: Boolean) {
        _overdueExpanded.value = expanded
    }

    fun setPeriod(
        screen: ItemStatistic,
        period: TimePeriod
    ) {
        start(
            screen = screen,
            period = period,
            reset = false
        )
    }

    fun nextMonth(screen: ItemStatistic) {
        val month = period.value?.month
        val year = period.value?.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                period = month.incrementMonthPeriod(ivyContext, 1L, year),
                reset = false
            )
        }
    }

    fun previousMonth(screen: ItemStatistic) {
        val month = period.value?.month
        val year = period.value?.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                period = month.incrementMonthPeriod(ivyContext, -1L, year),
                reset = false
            )
        }
    }

    fun delete(screen: ItemStatistic) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            when {
                screen.accountId != null -> {
                    deleteAccount(screen.accountId)
                }
                screen.categoryId != null -> {
                    deleteCategory(screen.categoryId)
                }
            }

            TestIdlingResource.decrement()
        }
    }

    private suspend fun deleteAccount(accountId: UUID) {
        ioThread {
            transactionDao.flagDeletedByAccountId(accountId = accountId)
            plannedPaymentRuleDao.flagDeletedByAccountId(accountId = accountId)
            accountDao.flagDeleted(accountId)

            nav.back()

            //the server deletes transactions + planned payments for the account
            accountUploader.delete(accountId)
        }
    }

    private suspend fun deleteCategory(categoryId: UUID) {
        ioThread {
            categoryDao.flagDeleted(categoryId)

            nav.back()

            categoryUploader.delete(categoryId)
        }
    }

    fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            categoryCreator.editCategory(updatedCategory) {
                _category.value = it
            }

            TestIdlingResource.decrement()
        }
    }

    fun editAccount(screen: ItemStatistic, account: Account, newBalance: Double) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.editAccount(account, newBalance) {
                start(
                    screen = screen,
                    period = period.value ?: ivyContext.selectedPeriod,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun payOrGet(screen: ItemStatistic, transaction: Transaction) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start(
                    screen = screen,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }
}