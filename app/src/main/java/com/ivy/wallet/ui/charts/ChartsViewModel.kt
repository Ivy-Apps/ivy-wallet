package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.then
import com.ivy.wallet.domain.action.charts.BalanceChartAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.WalletCategoryLogic
import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.domain.pure.charts.IncomeExpenseChartPoint
import com.ivy.wallet.domain.pure.charts.SingleChartPoint
import com.ivy.wallet.domain.pure.charts.incomeExpenseChart
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.utils.getDefaultFIATCurrency
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val walletCategoryLogic: WalletCategoryLogic,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val balanceChartAct: BalanceChartAct
) : ViewModel() {

    private val _period = MutableStateFlow(ChartPeriod.LAST_12_MONTHS)
    val period = _period.asStateFlow()

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    // ----------------------------------- Wallet --------------------------------------------------
    private val _balanceChart = MutableStateFlow(emptyList<SingleChartPoint>())
    val balanceChart = _balanceChart.asStateFlow()

    private val _incomeExpenseChart = MutableStateFlow(emptyList<IncomeExpenseChartPoint>())
    val incomeExpenseChart = _incomeExpenseChart
    // ----------------------------------- Wallet --------------------------------------------------


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
//            _baseCurrencyCode.value = ioThread { baseCurrencyCode(settingsDao) }

            walletCharts(period = period.value)
        }
    }

    private suspend fun walletCharts(period: ChartPeriod) {
        _balanceChart.value = generateBalanceChart(period)
        _incomeExpenseChart.value = generateIncomeExpenseChart(period)
    }

    private suspend fun generateBalanceChart(period: ChartPeriod) =
        (baseCurrencyAct then { baseCurrency ->
            balanceChartAct(
                BalanceChartAct.Input(
                    baseCurrency = baseCurrency,
                    period = period
                )
            )
        })(Unit)

    private suspend fun generateIncomeExpenseChart(period: ChartPeriod) = ioThread {
        incomeExpenseChart(
            walletDAOs = walletDAOs,
            baseCurrencyCode = baseCurrencyCode.value,
            period = period
        )
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
//        _categoryExpenseValues.value = categoryExpenseValues.loadCategoryValue(
//            period = period,
//            category = category,
//            calculateValue = { range ->
//                walletCategoryLogic.calculateCategoryExpenses(
//                    category = category,
//                    range = range
//                ).absoluteValue
//            }
//        )
    }

    private suspend fun loadCategoryExpenseCount(
        period: ChartPeriod,
        category: Category
    ) {
        _categoryExpenseCount.value = categoryExpenseCount.loadCategoryValue(
            period = period,
            category = category,
            calculateValue =  { range ->
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
        calculateValue: suspend (range: FromToTimeRange) -> Double
    ): List<CategoryValues> {
        TODO()
//        val values = ioThread {
//            period.toRangesList().map { range ->
//                TimeValue(
//                    range = range,
//                    period = period,
//                    value = calculateValue(range)
//                )
//            }
//        }
//
//        return this.value.plus(
//            CategoryValues(
//                category = category,
//                values = values
//            )
//        ).toSet().toList()
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

