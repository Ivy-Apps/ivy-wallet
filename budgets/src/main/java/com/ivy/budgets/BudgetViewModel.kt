package com.ivy.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.base.TimePeriod
import com.ivy.base.parseAccountIds
import com.ivy.base.parseCategoryIds
import com.ivy.base.toCloseTimeRange
import com.ivy.budgets.model.DisplayBudget
import com.ivy.data.Account
import com.ivy.data.Budget
import com.ivy.data.Category
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.exchange.ExchangeAct
import com.ivy.exchange.ExchangeData
import com.ivy.frp.sumOfSuspend
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.budget.BudgetsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.HistoryTrnsAct
import com.ivy.wallet.domain.deprecated.logic.BudgetCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.domain.deprecated.sync.item.BudgetSync
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.BudgetDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.readOnly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val settingsDao: SettingsDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val budgetCreator: BudgetCreator,
    private val budgetSync: BudgetSync,
    private val ivyContext: com.ivy.base.IvyWalletCtx,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val budgetsAct: BudgetsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val historyTrnsAct: HistoryTrnsAct,
    private val exchangeAct: ExchangeAct
) : ViewModel() {

    private val _timeRange = MutableStateFlow(ivyContext.selectedPeriod.toRange(1))
    val timeRange = _timeRange.readOnly()

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _budgets = MutableStateFlow<List<DisplayBudget>>(emptyList())
    val budgets = _budgets.readOnly()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.readOnly()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.readOnly()

    private val _categoryBudgetsTotal = MutableStateFlow(0.0)
    val categoryBudgetsTotal = _categoryBudgetsTotal.readOnly()

    private val _appBudgetMax = MutableStateFlow(0.0)
    val appBudgetMax = _appBudgetMax.readOnly()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _categories.value = categoriesAct(Unit)

            val accounts = accountsAct(Unit)
            _accounts.value = accounts

            val baseCurrency = baseCurrencyAct(Unit)
            _baseCurrencyCode.value = baseCurrency

            val startDateOfMonth = ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            val timeRange = TimePeriod.currentMonth(
                startDayOfMonth = startDateOfMonth
            ).toRange(startDateOfMonth = startDateOfMonth)
            _timeRange.value = timeRange

            val budgets = budgetsAct(Unit)

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
                            transactions = historyTrnsAct(timeRange.toCloseTimeRange()),
                            accounts = accounts,
                            baseCurrencyCode = baseCurrency
                        )
                    )
                }
            }!!

            TestIdlingResource.decrement()
        }
    }

    private suspend fun calculateSpentAmount(
        budget: Budget,
        transactions: List<Transaction>,
        baseCurrencyCode: String,
        accounts: List<Account>
    ): Double {
        //TODO: Re-work this by creating an FPAction for it
        val accountsFilter = budget.parseAccountIds()
        val categoryFilter = budget.parseCategoryIds()

        return transactions
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.accountId) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.categoryId) }
            .sumOfSuspend {
                when (it.type) {
                    TransactionType.INCOME -> {
                        //decrement spent amount if it's not global budget
                        0.0 //ignore income
//                        if (categoryFilter.isEmpty()) 0.0 else -amountBaseCurrency
                    }
                    TransactionType.EXPENSE -> {
                        //increment spent amount
                        exchangeAct(
                            ExchangeAct.Input(
                                data = ExchangeData(
                                    baseCurrency = baseCurrencyCode,
                                    fromCurrency = trnCurrency(it, accounts, baseCurrencyCode)
                                ),
                                amount = it.amount
                            )
                        ).orNull()?.toDouble() ?: 0.0
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
            TestIdlingResource.increment()

            budgetCreator.createBudget(data) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }

    fun editBudget(budget: Budget) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            budgetCreator.editBudget(budget) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            budgetCreator.deleteBudget(budget) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }


    fun reorder(newOrder: List<DisplayBudget>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.forEachIndexed { index, item ->
                    budgetDao.save(
                        item.budget.toEntity().copy(
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

            TestIdlingResource.decrement()
        }
    }
}