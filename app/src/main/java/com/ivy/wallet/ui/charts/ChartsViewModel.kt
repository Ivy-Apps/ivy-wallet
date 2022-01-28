package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.endOfMonth
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.WalletCategoryLogic
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _categoryValues = MutableStateFlow(emptyMap<Category, List<TimeValue>>())
    val categoryValues = _categoryValues.asStateFlow()

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
            val values = ioThread {
                lastNMonths(12)
                    .map { endOfMonth ->
                        TimeValue(
                            dateTime = endOfMonth,
                            value = walletCategoryLogic.calculateCategoryBalance(
                                category = category,
                                range = FromToTimeRange(
                                    from = endOfMonth.withDayOfMonth(1),
                                    to = endOfMonth
                                )
                            ).absoluteValue
                        )
                    }
            }

            _categoryValues.value = categoryValues.value.plus(
                Pair(
                    category, values
                )
            )
        }
    }

    fun removeCategory(category: Category) {
        _categoryValues.value = categoryValues.value.minus(category)
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

