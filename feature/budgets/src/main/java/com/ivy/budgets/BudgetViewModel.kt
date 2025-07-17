package com.ivy.budgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.budgets.model.DisplayBudget
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.model.Category
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.temp.migration.getAccountId
import com.ivy.data.temp.migration.getValue
import com.ivy.frp.sumOfSuspend
import com.ivy.legacy.data.model.FromToTimeRange
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Budget
import com.ivy.legacy.domain.deprecated.logic.BudgetCreator
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.budget.BudgetsAct
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@Stable
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val budgetWriter: WriteBudgetDao,
    private val budgetCreator: BudgetCreator,
    private val ivyContext: com.ivy.legacy.IvyWalletCtx,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val budgetsAct: BudgetsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val historyTrnsAct: HistoryTrnsAct,
    private val exchangeAct: ExchangeAct,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<BudgetScreenState, BudgetScreenEvent>() {

    private val baseCurrency = mutableStateOf("")
    private val timeRange = mutableStateOf<FromToTimeRange?>(null)
    private val budgets = mutableStateOf<ImmutableList<DisplayBudget>>(persistentListOf())
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categoryBudgetsTotal = mutableDoubleStateOf(0.0)
    private val appBudgetMax = mutableDoubleStateOf(0.0)
    private val totalRemainingBudget = mutableDoubleStateOf(0.0)
    private val reorderModalVisible = mutableStateOf(false)
    private val budgetModalData = mutableStateOf<BudgetModalData?>(null)

    @Composable
    override fun uiState(): BudgetScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return BudgetScreenState(
            baseCurrency = getBaseCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            budgets = getBudgets(),
            categoryBudgetsTotal = getCategoryBudgetsTotal(),
            appBudgetMax = getAppBudgetMax(),
            totalRemainingBudgetText = getTotalRemainingBudgetText(),
            timeRange = getTimeRange(),
            reorderModalVisible = getReorderModalVisible(),
            budgetModalData = getBudgetModalData()
        )
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getTimeRange(): FromToTimeRange? {
        return timeRange.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts.value
    }

    @Composable
    private fun getBudgets(): ImmutableList<DisplayBudget> {
        return budgets.value
    }

    @Composable
    private fun getReorderModalVisible(): Boolean {
        return reorderModalVisible.value
    }

    @Composable
    private fun getCategoryBudgetsTotal(): Double {
        return categoryBudgetsTotal.doubleValue
    }

    @Composable
    private fun getAppBudgetMax(): Double {
        return appBudgetMax.doubleValue
    }

    @Composable
    private fun getTotalRemainingBudgetText(): String? {
        val budgetExceeded = totalRemainingBudget.doubleValue < 0
        return when {
            categoryBudgetsTotal.doubleValue > 0 -> stringResource(
                if (budgetExceeded) R.string.budget_exceeded_info else R.string.total_budget_info,
                abs(totalRemainingBudget.doubleValue).format(baseCurrency.value),
                baseCurrency.value
            )

            else -> null
        }
    }

    @Composable
    private fun getBudgetModalData(): BudgetModalData? {
        return budgetModalData.value
    }

    override fun onEvent(event: BudgetScreenEvent) {
        when (event) {
            is BudgetScreenEvent.OnCreateBudget -> {
                createBudget(event.budgetData)
            }

            is BudgetScreenEvent.OnEditBudget -> {
                editBudget(event.budget)
            }

            is BudgetScreenEvent.OnDeleteBudget -> {
                deleteBudget(event.budget)
            }

            is BudgetScreenEvent.OnReorder -> {
                reorder(event.newOrder)
            }

            is BudgetScreenEvent.OnReorderModalVisible -> {
                reorderModalVisible.value = event.visible
            }

            is BudgetScreenEvent.OnBudgetModalData -> {
                budgetModalData.value = event.budgetModalData
            }
        }
    }

    private fun start() {
        viewModelScope.launch {
            categories.value = categoryRepository.findAll().toImmutableList()
            val accounts = accountsAct(Unit)
            val baseCurrency = baseCurrencyAct(Unit)
            val startDateOfMonth = ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            val timeRange = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = startDateOfMonth
            ).toRange(startDateOfMonth = startDateOfMonth, timeConverter, timeProvider)
            val budgets = budgetsAct(Unit)

            appBudgetMax.doubleValue = budgets
                .filter { it.categoryIdsSerialized.isNullOrBlank() }
                .maxOfOrNull { it.amount } ?: 0.0

            categoryBudgetsTotal.doubleValue = budgets
                .filter { it.categoryIdsSerialized.isNotNullOrBlank() }
                .sumOf { it.amount }

            this@BudgetViewModel.budgets.value = com.ivy.legacy.utils.ioThread {
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
            }
            totalRemainingBudget.doubleValue = calculateTotalRemainingBudget(
                budgets = this@BudgetViewModel.budgets.value,
                categoryBudgetsTotal = categoryBudgetsTotal.doubleValue
            )
            this@BudgetViewModel.accounts.value = accounts
            this@BudgetViewModel.baseCurrency.value = baseCurrency
            this@BudgetViewModel.timeRange.value = timeRange
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
            .filter { accountsFilter.isEmpty() || accountsFilter.contains(it.getAccountId()) }
            .filter { categoryFilter.isEmpty() || categoryFilter.contains(it.category?.value) }
            .sumOfSuspend {
                when (it) {
                    is Income -> {
                        0.0 // ignore income
                    }

                    is Expense -> {
                        // increment spent amount
                        exchangeAct(
                            ExchangeAct.Input(
                                data = ExchangeData(
                                    baseCurrency = baseCurrencyCode,
                                    fromCurrency = trnCurrency(it, accounts, baseCurrencyCode)
                                ),
                                amount = it.getValue()
                            )
                        ).orNull()?.toDouble() ?: 0.0
                    }

                    is Transfer -> {
                        // ignore transfers for simplicity
                        0.0
                    }
                }
            }
    }

    private fun createBudget(data: CreateBudgetData) {
        viewModelScope.launch {
            budgetCreator.createBudget(data) {
                start()
            }
        }
    }

    private fun editBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.editBudget(budget) {
                start()
            }
        }
    }

    private fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetCreator.deleteBudget(budget) {
                start()
            }
        }
    }

    private fun reorder(newOrder: List<DisplayBudget>) {
        viewModelScope.launch {
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
        }
    }
}

fun calculateTotalRemainingBudget(
    budgets: ImmutableList<DisplayBudget>,
    categoryBudgetsTotal: Double
): Double {
    return categoryBudgetsTotal - budgets
        .filter { it.budget.categoryIdsSerialized.isNotNullOrBlank() }
        .sumOf { it.spentAmount }
}
