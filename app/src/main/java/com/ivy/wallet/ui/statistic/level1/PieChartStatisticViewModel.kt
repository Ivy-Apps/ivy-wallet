package com.ivy.wallet.ui.statistic.level1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.readOnly
import com.ivy.wallet.functional.data.WalletDAOs
import com.ivy.wallet.functional.wallet.calculateWalletExpense
import com.ivy.wallet.functional.wallet.calculateWalletIncome
import com.ivy.wallet.logic.WalletCategoryLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val categoryLogic: WalletCategoryLogic,
    private val ivyContext: IvyContext
) : ViewModel() {
    private val _period = MutableStateFlow(ivyContext.selectedPeriod)
    val period = _period.readOnly()

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    val type = _type.readOnly()

    private val _baseCurrencyCode = MutableStateFlow("")
    val baseCurrencyCode = _baseCurrencyCode.readOnly()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount = _totalAmount.readOnly()

    private val _categoryAmounts = MutableStateFlow<List<CategoryAmount>>(emptyList())
    val categoryAmounts = _categoryAmounts.readOnly()

    private val _selectedCategory = MutableStateFlow<SelectedCategory?>(null)
    val selectedCategory = _selectedCategory.readOnly()

    fun start(
        screen: Screen.PieChartStatistic
    ) {
        load(
            period = ivyContext.selectedPeriod,
            type = screen.type
        )
    }

    private fun load(
        period: TimePeriod,
        type: TransactionType
    ) {
        _period.value = period
        val range = period.toRange(ivyContext.startDayOfMonth)
        _type.value = type

        _selectedCategory.value = null

        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }

            _baseCurrencyCode.value = settings.currency

            _totalAmount.value = ioThread {
                when (type) {
                    TransactionType.INCOME -> {
                        calculateWalletIncome(
                            walletDAOs = walletDAOs,
                            baseCurrencyCode = baseCurrencyCode.value,
                            range = range.toCloseTimeRange()
                        ).value.toDouble()
                    }
                    TransactionType.EXPENSE -> {
                        calculateWalletExpense(
                            walletDAOs = walletDAOs,
                            baseCurrencyCode = baseCurrencyCode.value,
                            range = range.toCloseTimeRange()
                        ).value.toDouble()
                    }
                    else -> error("not supported transactionType - $type")
                }
            }.absoluteValue

            _categoryAmounts.value = ioThread {
                categoryDao
                    .findAll()
                    .map {
                        CategoryAmount(
                            category = it,
                            amount = when (type) {
                                TransactionType.INCOME -> categoryLogic.calculateCategoryIncome(
                                    category = it,
                                    range = range
                                )
                                TransactionType.EXPENSE -> categoryLogic.calculateCategoryExpenses(
                                    category = it,
                                    range = range
                                )
                                else -> error("not supported transactionType - $type")
                            }
                        )
                    }
                    .plus(
                        //Unspecified
                        CategoryAmount(
                            category = null,
                            amount = when (type) {
                                TransactionType.INCOME -> categoryLogic.calculateUnspecifiedIncome(
                                    range = range
                                )
                                TransactionType.EXPENSE -> categoryLogic.calculateUnspecifiedExpenses(
                                    range = range
                                )
                                else -> error("not supported transactionType - $type")
                            }
                        )
                    )
                    .sortedByDescending { it.amount }
            }
        }
    }

    fun setSelectedCategory(selectedCategory: SelectedCategory?) {
        _selectedCategory.value = selectedCategory

        val categoryAmounts = _categoryAmounts.value
        _categoryAmounts.value = if (selectedCategory != null) {
            categoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedCategory.category == it.category
                }
        } else {
            categoryAmounts.sortedByDescending {
                it.amount
            }
        }
    }

    fun onSetPeriod(period: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(period)
        load(
            period = period,
            type = type.value
        )
    }

    fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year),
                type = type.value
            )
        }
    }

    fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year),
                type = type.value
            )
        }
    }
}