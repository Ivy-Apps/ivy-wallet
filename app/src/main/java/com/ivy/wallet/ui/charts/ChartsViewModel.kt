package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.orNull
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.functional.calculateBalance
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.logic.WalletCategoryLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val exchangeRateDao: ExchangeRateDao,

    private val walletLogic: WalletLogic,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val walletCategoryLogic: WalletCategoryLogic
) : ViewModel() {

    private val _period = MutableStateFlow(ChartPeriod.LAST_12_MONTHS)
    val period = _period.asStateFlow()

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _balanceValues = MutableStateFlow(emptyList<TimeValue>())
    val balanceValues = _balanceValues.asStateFlow()

    private val _incomeValues = MutableStateFlow(emptyList<TimeValue>())
    val incomeValues = _incomeValues.asStateFlow()

    private val _expenseValues = MutableStateFlow(emptyList<TimeValue>())
    val expenseValues = _expenseValues.asStateFlow()

    // --------------------------- Category --------------------------------------------------------
    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories = _categories.asStateFlow()

    private val _categoryExpenseValues = MutableStateFlow(emptyList<CategoryValues>())
    val categoryExpenseValues = _categoryExpenseValues.asStateFlow()

    private val _categoryExpenseCount = MutableStateFlow(emptyList<CategoryValues>())
    val categoryExpenseCount = _categoryExpenseCount.asStateFlow()

    private val _categoryIncomeValues = MutableStateFlow(emptyList<CategoryValues>())
    val categoryIncomeValues = _categoryIncomeValues.asStateFlow()

    private val _categoryIncomeCount = MutableStateFlow(emptyList<CategoryValues>())
    val categoryIncomeCount = _categoryIncomeCount.asStateFlow()
    // --------------------------- Category --------------------------------------------------------

    // --------------------------- Accounts --------------------------------------------------------
    //TODO: Implement
    // --------------------------- Accounts --------------------------------------------------------


    fun start() {
        viewModelScope.launch {
            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            val period = period.value
            val periodRangesList = period.toRangesList()

            _balanceValues.value = ioThread {
                periodRangesList.map { range ->
                    TimeValue(
                        range = range,
                        period = period,
                        value = calculateBalance(
                            accountDao = accountDao,
                            transactionDao = transactionDao,
                            exchangeRateDao = exchangeRateDao,
                            baseCurrencyCode = baseCurrencyCode.value,
                            range = ClosedTimeRange.to(range.to())
                        ).orNull()?.toDouble() ?: 0.0
                    )
                }
            }

            _incomeValues.value = ioThread {
                periodRangesList.map { range ->
                    TimeValue(
                        range = range,
                        period = period,
                        value = walletLogic.calculateIncome(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = range.from(),
                                    to = range.to()
                                )
                            ).filterIsInstance(Transaction::class.java)
                        )
                    )
                }
            }

            _expenseValues.value = ioThread {
                periodRangesList.map { range ->
                    TimeValue(
                        range = range,
                        period = period,
                        value = walletLogic.calculateExpenses(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = range.from(),
                                    to = range.to()
                                )
                            ).filterIsInstance(Transaction::class.java)
                        )
                    )
                }
            }

            _categories.value = ioThread {
                categoryDao.findAll()
            }
        }
    }


    fun loadValuesForCategory(
        category: Category
    ) {
        viewModelScope.launch {
            val period = period.value

            loadCategoryExpenseValues(
                period = period,
                category = category
            )

            loadCategoryExpenseCount(
                period = period,
                category = category
            )

            loadCategoryIncomeValues(
                period = period,
                category = category
            )

            loadCategoryIncomeCount(
                period = period,
                category = category
            )
        }
    }

    private suspend fun loadCategoryExpenseValues(
        period: ChartPeriod,
        category: Category
    ) {
        _categoryExpenseValues.value = categoryExpenseValues.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { range ->
                walletCategoryLogic.calculateCategoryExpenses(
                    category = category,
                    range = range
                ).absoluteValue
            }
        )
    }

    private suspend fun loadCategoryExpenseCount(
        period: ChartPeriod,
        category: Category
    ) {
        _categoryExpenseCount.value = categoryExpenseCount.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { range ->
                walletCategoryLogic.historyByCategory(
                    category = category,
                    range = range
                ).count { it.type == TransactionType.EXPENSE }.toDouble()
            }
        )
    }

    private suspend fun loadCategoryIncomeValues(
        period: ChartPeriod,
        category: Category
    ) {
        _categoryIncomeValues.value = categoryIncomeValues.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { range ->
                walletCategoryLogic.calculateCategoryIncome(
                    category = category,
                    range = range
                )
            }
        )
    }

    private suspend fun loadCategoryIncomeCount(
        period: ChartPeriod,
        category: Category
    ) {
        _categoryIncomeCount.value = categoryIncomeCount.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { range ->
                walletCategoryLogic.historyByCategory(
                    category = category,
                    range = range
                ).count { it.type == TransactionType.INCOME }.toDouble()
            }
        )
    }


    private suspend fun StateFlow<List<CategoryValues>>.loadCategoryValue(
        period: ChartPeriod,
        category: Category,
        calculateValue: (range: FromToTimeRange) -> Double
    ): List<CategoryValues> {
        val values = ioThread {
            period.toRangesList().map { range ->
                TimeValue(
                    range = range,
                    period = period,
                    value = calculateValue(range)
                )
            }
        }

        return this.value.plus(
            CategoryValues(
                category = category,
                values = values
            )
        ).toSet().toList()
    }

    fun removeCategory(category: Category) {
        _categoryExpenseValues.value =
            categoryExpenseValues.value.filter { it.category != category }
        _categoryExpenseCount.value = categoryExpenseCount.value.filter { it.category != category }
        _categoryIncomeValues.value = categoryIncomeValues.value.filter { it.category != category }
        _categoryIncomeCount.value = categoryIncomeCount.value.filter { it.category != category }
    }

    fun changePeriod(period: ChartPeriod) {
        _period.value = period
        start()

        //Re-load categories
        val loadedCategories = categoryExpenseValues.value.map { it.category }
        loadedCategories.forEach { removeCategory(it) }
        loadedCategories.forEach { loadValuesForCategory(it) }
    }
}

