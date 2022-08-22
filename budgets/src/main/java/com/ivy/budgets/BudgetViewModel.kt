package com.ivy.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.base.toCloseTimeRange
import com.ivy.budgets.model.DisplayBudget
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.core.ui.temp.trash.parseAccountIds
import com.ivy.core.ui.temp.trash.parseCategoryIds
import com.ivy.data.AccountOld
import com.ivy.data.Budget
import com.ivy.data.CategoryOld
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.exchange.deprecated.ExchangeActOld
import com.ivy.exchange.deprecated.ExchangeData
import com.ivy.frp.sumOfSuspend
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.budget.BudgetsAct
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyActOld
import com.ivy.wallet.domain.action.transaction.HistoryTrnsAct
import com.ivy.wallet.domain.deprecated.logic.BudgetCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.domain.deprecated.sync.item.BudgetSync
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import com.ivy.wallet.io.persistence.dao.BudgetDao
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
    private val budgetDao: BudgetDao,
    private val budgetCreator: BudgetCreator,
    private val budgetSync: BudgetSync,
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val accountsAct: AccountsActOld,
    private val categoriesAct: CategoriesActOld,
    private val budgetsAct: BudgetsAct,
    private val baseCurrencyAct: BaseCurrencyActOld,
    private val historyTrnsAct: HistoryTrnsAct,
    private val exchangeAct: ExchangeActOld,
    private val startDayOfMonthAct: StartDayOfMonthAct,
) : ViewModel() {

    private val _timeRange = MutableStateFlow(ivyContext.selectedPeriod.toRange(1))
    val timeRange = _timeRange.readOnly()

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _budgets = MutableStateFlow<List<DisplayBudget>>(emptyList())
    val budgets = _budgets.readOnly()

    private val _categories = MutableStateFlow<List<CategoryOld>>(emptyList())
    val categories = _categories.readOnly()

    private val _accounts = MutableStateFlow<List<AccountOld>>(emptyList())
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

            val startDateOfMonth = startDayOfMonthAct(Unit)
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
        transactions: List<TransactionOld>,
        baseCurrencyCode: String,
        accounts: List<AccountOld>
    ): Double {
        //TODO: Re-work this by creating an FPAction for it
        val accountsFilter = budget.parseAccountIds()
        val categoryFilter = budget.parseCategoryIds()

        return transactions
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.accountId) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.categoryId) }
            .sumOfSuspend {
                when (it.type) {
                    TrnType.INCOME -> {
                        //decrement spent amount if it's not global budget
                        0.0 //ignore income
//                        if (categoryFilter.isEmpty()) 0.0 else -amountBaseCurrency
                    }
                    TrnType.EXPENSE -> {
                        //increment spent amount
                        exchangeAct(
                            ExchangeActOld.Input(
                                data = ExchangeData(
                                    baseCurrency = baseCurrencyCode,
                                    fromCurrency = trnCurrency(it, accounts, baseCurrencyCode)
                                ),
                                amount = it.amount
                            )
                        ).orNull()?.toDouble() ?: 0.0
                    }
                    TrnType.TRANSFER -> {
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