package com.ivy.wallet.ui.budget

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.isNotNullOrBlank
import com.ivy.wallet.logic.BudgetCreator
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateBudgetData
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Budget
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.BudgetDao
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.sync.item.BudgetSync
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.budget.model.DisplayBudget
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val settingsDao: SettingsDao,
    private val budgetDao: BudgetDao,
    private val walletLogic: WalletLogic,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val budgetCreator: BudgetCreator,
    private val budgetSync: BudgetSync,
    private val ivyContext: IvyContext
) : ViewModel() {

    private val _timeRange = MutableLiveData<FromToTimeRange>()
    val timeRange = _timeRange.asLiveData()

    private val _baseCurrencyCode = MutableLiveData(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asLiveData()

    private val _budgets = MutableLiveData<List<DisplayBudget>>()
    val budgets = _budgets.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    private val _categoryBudgetsTotal = MutableLiveData<Double>()
    val categoryBudgetsTotal = _categoryBudgetsTotal.asLiveData()

    private val _appBudgetMax = MutableLiveData<Double>()
    val appBudgetMax = _appBudgetMax.asLiveData()

    fun start() {
        viewModelScope.launch {
            _categories.value = ioThread {
                categoryDao.findAll()
            }!!

            val accounts = ioThread {
                accountDao.findAll()
            }
            _accounts.value = accounts

            val settings = ioThread {
                settingsDao.findFirst()
            }

            val baseCurrency = settings.currency
            _baseCurrencyCode.value = baseCurrency

            val startDateOfMonth = ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            val timeRange = TimePeriod.currentMonth(
                startDayOfMonth = startDateOfMonth
            ).toRange(startDateOfMonth = startDateOfMonth)
            _timeRange.value = timeRange

            val transactions = ioThread {
                walletLogic.history(range = timeRange)
            }.filterIsInstance(Transaction::class.java)

            val budgets = ioThread {
                budgetDao.findAll()
            }

            _appBudgetMax.value = budgets
                .filter { it.categoryIdsSerialized.isNullOrBlank() }
                .maxOfOrNull { it.amount } ?: 0.0

            _categoryBudgetsTotal.value = budgets
                .filter { it.categoryIdsSerialized.isNotNullOrBlank() }
                .sumOf { it.amount }

            _budgets.value = ioThread {
                budgets.map {
                    DisplayBudget(
                        budget = it,
                        spentAmount = calculateSpentAmount(
                            budget = it,
                            transactions = transactions,
                            accounts = accounts,
                            baseCurrencyCode = baseCurrency
                        )
                    )
                }
            }!!
        }
    }

    private fun calculateSpentAmount(
        budget: Budget,
        transactions: List<Transaction>,
        baseCurrencyCode: String,
        accounts: List<Account>
    ): Double {
        val accountsFilter = budget.parseAccountIds()
        val categoryFilter = budget.parseCategoryIds()

        return transactions
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.accountId) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.categoryId) }
            .sumOf {
                val amountBaseCurrency = exchangeRatesLogic.amountBaseCurrency(
                    transaction = it,
                    baseCurrency = baseCurrencyCode,
                    accounts = accounts
                )

                when (it.type) {
                    TransactionType.INCOME -> {
                        //decrement spent amount if it's not global budget
                        0.0 //ignore income
//                        if (categoryFilter.isEmpty()) 0.0 else -amountBaseCurrency
                    }
                    TransactionType.EXPENSE -> {
                        //increment spent amount
                        amountBaseCurrency
                    }
                    TransactionType.TRANSFER -> {
                        //ignore transfers for simplicity
                        0.0
                    }
                }
            }
    }

    fun createBudget(data: CreateBudgetData) {
        viewModelScope.launch {
            budgetCreator.createBudget(data) {
                start()
            }
        }
    }

    fun editBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.editBudget(budget) {
                start()
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.deleteBudget(budget) {
                start()
            }
        }
    }


    fun reorder(newOrder: List<DisplayBudget>) {
        viewModelScope.launch {
            ioThread {
                newOrder.forEachIndexed { index, item ->
                    budgetDao.save(
                        item.budget.copy(
                            orderId = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            ioThread {
                budgetSync.sync()
            }
        }
    }
}