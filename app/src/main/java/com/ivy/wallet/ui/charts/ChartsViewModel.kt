package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.endOfMonth
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.WalletCategoryLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val walletLogic: WalletLogic,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val walletCategoryLogic: WalletCategoryLogic
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _balanceValues = MutableStateFlow(emptyList<TimeValue>())
    val balanceValues = _balanceValues.asStateFlow()

    private val _incomeValues = MutableStateFlow(emptyList<TimeValue>())
    val incomeValues = _incomeValues.asStateFlow()

    private val _expenseValues = MutableStateFlow(emptyList<TimeValue>())
    val expenseValues = _expenseValues.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories = _categories.asStateFlow()

    private val _categoryExpenseValues = MutableStateFlow(emptyMap<Category, List<TimeValue>>())
    val categoryExpenseValues = _categoryExpenseValues.asStateFlow()

    private val _categoryExpenseCount = MutableStateFlow(emptyMap<Category, List<TimeValue>>())
    val categoryExpenseCount = _categoryExpenseCount.asStateFlow()

    private val _categoryIncomeValues = MutableStateFlow(emptyMap<Category, List<TimeValue>>())
    val categoryIncomeValues = _categoryIncomeValues.asStateFlow()

    private val _categoryIncomeCount = MutableStateFlow(emptyMap<Category, List<TimeValue>>())
    val categoryIncomeCount = _categoryIncomeCount.asStateFlow()

    fun start() {
        viewModelScope.launch {
            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            val lastNMonths = lastNMonths(n = 12)

            _balanceValues.value = ioThread {
                lastNMonths.map { month ->
                    TimeValue(
                        dateTime = month,
                        value = walletLogic.calculateBalance(
                            before = month
                        )
                    )
                }
            }

            _incomeValues.value = ioThread {
                lastNMonths.map { endOfMonthTime ->
                    TimeValue(
                        dateTime = endOfMonthTime,
                        value = walletLogic.calculateIncome(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = endOfMonthTime.withDayOfMonth(1),
                                    to = endOfMonthTime
                                )
                            ).filterIsInstance(Transaction::class.java)
                        )
                    )
                }
            }

            _expenseValues.value = ioThread {
                lastNMonths.map { endOfMonthTime ->
                    TimeValue(
                        dateTime = endOfMonthTime,
                        value = walletLogic.calculateExpenses(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = endOfMonthTime.withDayOfMonth(1),
                                    to = endOfMonthTime
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


    fun loadValuesForCategory(category: Category) {
        viewModelScope.launch {
            val lastNMonths = lastNMonths(12)

            loadCategoryExpenseValues(
                period = lastNMonths,
                category = category
            )

            loadCategoryExpenseCount(
                period = lastNMonths,
                category = category
            )

            loadCategoryIncomeValues(
                period = lastNMonths,
                category = category
            )

            loadCategoryIncomeCount(
                period = lastNMonths,
                category = category
            )
        }
    }

    private suspend fun loadCategoryExpenseValues(
        period: List<LocalDateTime>,
        category: Category
    ) {
        _categoryExpenseValues.value = categoryExpenseValues.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { endOfMonth ->
                walletCategoryLogic.calculateCategoryExpenses(
                    category = category,
                    range = FromToTimeRange(
                        from = endOfMonth.withDayOfMonth(1),
                        to = endOfMonth
                    )
                ).absoluteValue
            }
        )
    }

    private suspend fun loadCategoryExpenseCount(
        period: List<LocalDateTime>,
        category: Category
    ) {
        _categoryExpenseCount.value = categoryExpenseCount.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { endOfMonth ->
                walletCategoryLogic.historyByCategory(
                    category = category,
                    range = FromToTimeRange(
                        from = endOfMonth.withDayOfMonth(1),
                        to = endOfMonth
                    )
                ).count { it.type == TransactionType.EXPENSE }.toDouble()
            }
        )
    }

    private suspend fun loadCategoryIncomeValues(
        period: List<LocalDateTime>,
        category: Category
    ) {
        _categoryIncomeValues.value = categoryIncomeValues.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { endOfMonth ->
                walletCategoryLogic.calculateCategoryIncome(
                    category = category,
                    range = FromToTimeRange(
                        from = endOfMonth.withDayOfMonth(1),
                        to = endOfMonth
                    )
                )
            }
        )
    }

    private suspend fun loadCategoryIncomeCount(
        period: List<LocalDateTime>,
        category: Category
    ) {
        _categoryIncomeCount.value = categoryIncomeCount.loadCategoryValue(
            period = period,
            category = category,
            calculateValue = { endOfMonth ->
                walletCategoryLogic.historyByCategory(
                    category = category,
                    range = FromToTimeRange(
                        from = endOfMonth.withDayOfMonth(1),
                        to = endOfMonth
                    )
                ).count { it.type == TransactionType.INCOME }.toDouble()
            }
        )
    }


    private suspend fun StateFlow<Map<Category, List<TimeValue>>>.loadCategoryValue(
        period: List<LocalDateTime>,
        category: Category,
        calculateValue: (endOfMonth: LocalDateTime) -> Double
    ): Map<Category, List<TimeValue>> {
        val values = ioThread {
            period.map { endOfMonth ->
                TimeValue(
                    dateTime = endOfMonth,
                    value = calculateValue(endOfMonth)
                )
            }
        }

        return this.value.plus(
            Pair(
                category, values
            )
        )

    }

    fun removeCategory(category: Category) {
        _categoryExpenseValues.value = categoryExpenseValues.value.minus(category)
        _categoryExpenseCount.value = categoryExpenseCount.value.minus(category)
        _categoryIncomeValues.value = categoryIncomeValues.value.minus(category)
        _categoryIncomeCount.value = categoryIncomeCount.value.minus(category)
    }

    private fun lastNMonths(
        n: Int,
        accumulator: List<LocalDateTime> = emptyList(),
        date: LocalDateTime = endOfMonth(dateNowUTC())
    ): List<LocalDateTime> {
        return if (accumulator.size < n) {
            //recurse
            lastNMonths(
                n = n,
                accumulator = accumulator.plus(date),
                date = endOfMonth(date.withDayOfMonth(10).minusMonths(1).toLocalDate())
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }
}

