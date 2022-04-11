package com.ivy.wallet.ui.statistic.level1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Category
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.category.calculateCategoryExpenseWithAccountFilters
import com.ivy.wallet.domain.fp.category.calculateCategoryIncomeWithAccountFilters
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.domain.fp.wallet.calculateWalletExpenseWithAccountFilters
import com.ivy.wallet.domain.fp.wallet.calculateWalletIncomeWithAccountFilters
import com.ivy.wallet.domain.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.logic.currency.sumInBaseCurrency
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.PieChartStatistic
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import com.ivy.wallet.utils.scopedIOThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val transactionDao: TransactionDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val ivyContext: IvyWalletCtx
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

    private val _accountFilterList = MutableStateFlow<List<UUID>>(emptyList())
    val accountIdFilterList = _accountFilterList.readOnly()

    private val _showCloseButtonOnly = MutableStateFlow(false)
    val showCloseButtonOnly = _showCloseButtonOnly.readOnly()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transaction = _transactions.readOnly()

    private var filterExcluded = true

    fun start(
        screen: PieChartStatistic
    ) {
        if (screen.transactions.isEmpty()) {
            initPieChartNormally(
                period = ivyContext.selectedPeriod,
                type = screen.type,
                accountList = screen.accountList,
                filterExcluded = screen.filterExcluded
            )
        } else {
            initPieChartFormTransactions(
                period = ivyContext.selectedPeriod,
                type = screen.type,
                accountFilterList = screen.accountList,
                filterExclude = screen.filterExcluded,
                transactions = screen.transactions
            )
        }
    }

    private fun initPieChartNormally(
        period: TimePeriod,
        type: TransactionType,
        accountList: List<UUID>,
        filterExcluded: Boolean
    ) {
        _showCloseButtonOnly.value = false
        _accountFilterList.value = accountList
        this.filterExcluded = filterExcluded
        _transactions.value = emptyList()

        load(
            period = period,
            type = type,
            accountFilterList = accountList
        )
    }

    private fun initPieChartFormTransactions(
        period: TimePeriod,
        type: TransactionType,
        accountFilterList: List<UUID>,
        filterExclude: Boolean,
        transactions: List<Transaction>
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _showCloseButtonOnly.value = true
            _accountFilterList.value = accountFilterList
            _baseCurrencyCode.value = ioThread { settingsDao.findFirst() }.currency
            _transactions.value = transactions
            filterExcluded = filterExclude

            val catAmounts = scopedIOThread { scope ->
                transactions.groupBy { it.categoryId }.map { mapEntry ->
                    scope.async {
                        val category =
                            if (mapEntry.key == null) null else categoryDao.findById(mapEntry.key!!)

                        val amount = mapEntry.value.filter { it.type == type }.sumInBaseCurrency(
                            exchangeRatesLogic = exchangeRatesLogic,
                            settingsDao = settingsDao,
                            accountDao = walletDAOs.accountDao
                        )

                        CategoryAmount(category, amount)
                    }
                }.awaitAll().sortedByDescending { it.amount }
            }

            _totalAmount.value = catAmounts.sumOf { it.amount }
            _categoryAmounts.value = catAmounts
        }
    }

    private fun load(
        period: TimePeriod,
        type: TransactionType,
        accountFilterList: List<UUID>
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
                        calculateWalletIncomeWithAccountFilters(
                            walletDAOs = walletDAOs,
                            baseCurrencyCode = baseCurrencyCode.value,
                            range = range.toCloseTimeRange(),
                            accountIdFilterList = accountFilterList,
                            filterExcluded = filterExcluded
                        ).value.toDouble()
                    }
                    TransactionType.EXPENSE -> {
                        calculateWalletExpenseWithAccountFilters(
                            walletDAOs = walletDAOs,
                            baseCurrencyCode = baseCurrencyCode.value,
                            range = range.toCloseTimeRange(),
                            accountIdFilterList = accountFilterList,
                            filterExcluded = filterExcluded
                        ).value.toDouble()
                    }
                    else -> error("not supported transactionType - $type")
                }
            }.absoluteValue

            _categoryAmounts.value = scopedIOThread { scope ->

                val categories =
                    getCategories(
                        fetchCategoriesFromTransactions = accountFilterList.isNotEmpty(),
                        timeRange = range
                    )

                categories
                    .plus(null) //for unspecified
                    .map { category ->
                        CategoryAmount(
                            category = category,
                            amount = when (type) {
                                TransactionType.INCOME -> {
                                    calculateCategoryIncomeWithAccountFilters(
                                        walletDAOs = walletDAOs,
                                        baseCurrencyCode = baseCurrencyCode.value,
                                        categoryId = category?.id,
                                        accountIdFilterList = accountFilterList,
                                        range = range.toCloseTimeRange()
                                    ).toDouble()
                                }
                                TransactionType.EXPENSE -> {
                                    calculateCategoryExpenseWithAccountFilters(
                                        walletDAOs = walletDAOs,
                                        baseCurrencyCode = baseCurrencyCode.value,
                                        categoryId = category?.id,
                                        accountIdList = accountFilterList,
                                        range = range.toCloseTimeRange()
                                    ).toDouble()
                                }
                                else -> error("not supported transactionType - $type")
                            }
                        )
                    }
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
            type = type.value,
            accountFilterList = accountIdFilterList.value
        )
    }

    fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year),
                type = type.value,
                accountFilterList = accountIdFilterList.value
            )
        }
    }

    fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year),
                type = type.value,
                accountFilterList = accountIdFilterList.value
            )
        }
    }

    private suspend fun getCategories(
        fetchCategoriesFromTransactions: Boolean,
        timeRange: FromToTimeRange
    ): List<Category> {
        return scopedIOThread { scope ->
            if (fetchCategoriesFromTransactions) {
                transactionDao.findAllBetween(timeRange.from(), timeRange.to()).filter {
                    it.categoryId != null
                }.map {
                    scope.async {
                        categoryDao.findById(it.categoryId!!)
                    }
                }.awaitAll().filterNotNull().distinctBy { it.id }
            } else
                categoryDao.findAll()
        }
    }
}