package com.ivy.wallet.ui.statistic.level1

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.R
import com.ivy.wallet.domain.action.charts.PieChartAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumInBaseCurrency
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.PieChartStatistic
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.IvyLight
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

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val transactionDao: TransactionDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val ivyContext: IvyWalletCtx,
    private val pieChartAct: PieChartAct,
    private val sharedPrefs: SharedPrefs
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

    private val _accountIdFilterList = MutableStateFlow<List<UUID>>(emptyList())
    val accountIdFilterList = _accountIdFilterList.readOnly()

    private val _showCloseButtonOnly = MutableStateFlow(false)
    val showCloseButtonOnly = _showCloseButtonOnly.readOnly()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transaction = _transactions.readOnly()

    private var filterExcluded = true
    private val transfersCategory =
        Category(
            stringRes(R.string.account_transfers),
            color = IvyLight.toArgb(),
            icon = "transfer"
        )

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
                accountIdFilterList = screen.accountList,
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
        _accountIdFilterList.value = accountList
        this.filterExcluded = filterExcluded
        _transactions.value = emptyList()

        load(
            period = period,
            type = type,
            accountIdFilterList = accountList
        )
    }

    private fun initPieChartFormTransactions(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: List<UUID>,
        filterExclude: Boolean,
        transactions: List<Transaction>
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _showCloseButtonOnly.value = true
            _accountIdFilterList.value = accountIdFilterList
            _baseCurrencyCode.value = ioThread { settingsDao.findFirst() }.currency
            _transactions.value = transactions
            _type.value = type
            filterExcluded = filterExclude

            val accountTransfersCategoryAmount =
                async { getAccountTransfersCategoryAmount(transactions, type, accountIdFilterList) }

            val catAmounts = scopedIOThread { scope ->
                transactions.groupBy { it.categoryId }.map { mapEntry ->
                    scope.async {
                        val category =
                            if (mapEntry.key == null) null else categoryDao.findById(mapEntry.key!!)

                        val trans = mapEntry.value.filter { it.type == type }

                        val amount = trans.sumInBaseCurrency(
                            exchangeRatesLogic = exchangeRatesLogic,
                            settingsDao = settingsDao,
                            accountDao = walletDAOs.accountDao
                        )

                        CategoryAmount(category?.toDomain(), amount, trans)
                    }
                }.awaitAll().sortedByDescending { it.amount }
            }

            _totalAmount.value = catAmounts.sumOf { it.amount }
            _categoryAmounts.value = listOf(accountTransfersCategoryAmount.await()) + catAmounts
        }
    }

    private suspend fun getAccountTransfersCategoryAmount(
        transactions: List<Transaction>,
        type: TransactionType,
        accountIdFilterList: List<UUID>
    ): CategoryAmount {

        val accountTransferTrans = transactions.filter {
            it.type == TransactionType.TRANSFER && it.categoryId == null
        }

        //Converted to set for faster filtering
        val accountIdFilterSet = accountIdFilterList.toHashSet()

        val categoryAmount = scopedIOThread {

            val trans = if (type == TransactionType.EXPENSE)
                accountTransferTrans.filter { accountIdFilterSet.contains(it.accountId) }
            else
                accountTransferTrans.filter { accountIdFilterSet.contains(it.toAccountId) }

            val amt = trans.sumOf {
                exchangeRatesLogic.toAmountBaseCurrency(
                    transaction = it,
                    baseCurrency = baseCurrencyCode.value,
                    accounts = walletDAOs.accountDao.findAll().map { it.toDomain() }
                )
            }
            CategoryAmount(transfersCategory, amt, trans)
        }
        return categoryAmount
    }

    private fun load(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: List<UUID>
    ) {

        _period.value = period
        val range = period.toRange(ivyContext.startDayOfMonth)
        _type.value = type

        _selectedCategory.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val settings = ioThread { settingsDao.findFirst() }
            _baseCurrencyCode.value = settings.currency

            val treatTransferAsIncExp =
                sharedPrefs.getBoolean(
                    SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                    false
                ) && accountIdFilterList.isNotEmpty()

            val pieChartActOutput = pieChartAct(
                PieChartAct.Input(
                    baseCurrency = _baseCurrencyCode.value,
                    range = range,
                    type = _type.value,
                    accountIdFilterList = accountIdFilterList,
                    treatTransferAsIncExp = treatTransferAsIncExp
                )
            )

            _totalAmount.value = pieChartActOutput.totalAmount
            _categoryAmounts.value = pieChartActOutput.categoryAmounts
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
            accountIdFilterList = accountIdFilterList.value
        )
    }

    fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year),
                type = type.value,
                accountIdFilterList = accountIdFilterList.value
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
                accountIdFilterList = accountIdFilterList.value
            )
        }
    }

    private suspend fun getCategories(
        fetchCategoriesFromTransactions: Boolean,
        timeRange: FromToTimeRange
    ): List<Category> {
        return scopedIOThread { scope ->
            if (fetchCategoriesFromTransactions) {
                transactionDao.findAllBetween(timeRange.from(), timeRange.to())
                    .map { it.toDomain() }
                    .filter {
                        it.categoryId != null
                    }.map {
                        scope.async {
                            categoryDao.findById(it.categoryId!!)?.toDomain()
                        }
                    }.awaitAll().filterNotNull().distinctBy { it.id }
            } else
                categoryDao.findAll().map { it.toDomain() }
        }
    }

    fun checkForUnspecifiedCategory(category: Category?): Boolean {
        return category == null || category == transfersCategory || category.name == stringRes(R.string.account_transfers)
    }
}