package com.ivy.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Transaction
import com.ivy.budgets.model.DisplayBudget
import com.ivy.frp.sumOfSuspend
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Budget
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.domain.deprecated.logic.BudgetCreator
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.readOnly
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.base.model.TransactionType
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.budget.BudgetsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.HistoryTrnsAct
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val budgetWriter: WriteBudgetDao,
    private val budgetCreator: BudgetCreator,
    private val ivyContext: com.ivy.legacy.IvyWalletCtx,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val budgetsAct: BudgetsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val historyTrnsAct: HistoryTrnsAct,
    private val exchangeAct: ExchangeAct
) : ViewModel() {

    private val _timeRange = MutableStateFlow(ivyContext.selectedPeriod.toRange(1))
    val timeRange = _timeRange.readOnly()

    private val _baseCurrencyCode =
        MutableStateFlow(com.ivy.legacy.utils.getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _budgets = MutableStateFlow<ImmutableList<DisplayBudget>>(persistentListOf())
    val budgets = _budgets.readOnly()

    private val _categories = MutableStateFlow<ImmutableList<Category>>(persistentListOf())
    val categories = _categories.readOnly()

    private val _accounts = MutableStateFlow<ImmutableList<Account>>(persistentListOf())
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
            val timeRange = com.ivy.legacy.data.model.TimePeriod.currentMonth(
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

            _budgets.value = com.ivy.legacy.utils.ioThread {
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
                }.toImmutableList()
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
        // TODO: Re-work this by creating an FPAction for it
        val accountsFilter = budget.parseAccountIds()
        val categoryFilter = budget.parseCategoryIds()

        return transactions
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.accountId) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.categoryId) }
            .sumOfSuspend {
                when (it.type) {
                    TransactionType.INCOME -> {
                        // decrement spent amount if it's not global budget
                        0.0 // ignore income
//                        if (categoryFilter.isEmpty()) 0.0 else -amountBaseCurrency
                    }

                    TransactionType.EXPENSE -> {
                        // increment spent amount
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
                        // ignore transfers for simplicity
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

            com.ivy.legacy.utils.ioThread {
                newOrder.forEachIndexed { index, item ->
                    budgetWriter.save(
                        item.budget.toEntity().copy(
                            orderId = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            TestIdlingResource.decrement()
        }
    }
}
