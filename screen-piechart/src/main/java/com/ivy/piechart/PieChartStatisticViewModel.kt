package com.ivy.piechart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.domain.ComposeViewModel
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.piechart.action.PieChartAct
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val pieChartAct: PieChartAct,
    private val sharedPrefs: SharedPrefs
) : ComposeViewModel<PieChartStatisticState, PieChartStatisticEvent>() {

    private val treatTransfersAsIncomeExpense = mutableStateOf(false)
    private val transactionType = mutableStateOf(TransactionType.INCOME)
    private val period = mutableStateOf(TimePeriod())
    private val baseCurrency = mutableStateOf("")
    private val totalAmount = mutableDoubleStateOf(0.0)
    private val categoryAmounts = mutableStateOf<ImmutableList<CategoryAmount>>(persistentListOf())
    private val selectedCategory = mutableStateOf<SelectedCategory?>(null)
    private val accountIdFilterList = mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private val showCloseButtonOnly = mutableStateOf(false)
    private val filterExcluded = mutableStateOf(false)
    private val transactions = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val choosePeriodModal = mutableStateOf<ChoosePeriodModalData?>(null)

    @Composable
    override fun uiState(): PieChartStatisticState {
        return PieChartStatisticState(
            transactionType = getTransactionType(),
            period = getPeriod(),
            baseCurrency = getBaseCurrency(),
            totalAmount = getTotalAmount(),
            categoryAmounts = getCategoryAmounts(),
            selectedCategory = getSelectedCategory(),
            accountIdFilterList = getAccountIdFilterList(),
            showCloseButtonOnly = getShowCloseButtonOnly(),
            filterExcluded = getFilterExcluded(),
            transactions = getTransactions(),
            choosePeriodModal = getChoosePeriodModal()
        )
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType.value
    }

    @Composable
    private fun getPeriod(): TimePeriod {
        return period.value
    }

    @Composable
    private fun getBaseCurrency(): String {
        return baseCurrency.value
    }

    @Composable
    private fun getTotalAmount(): Double {
        return totalAmount.doubleValue
    }

    @Composable
    private fun getCategoryAmounts(): ImmutableList<CategoryAmount> {
        return categoryAmounts.value
    }

    @Composable
    private fun getSelectedCategory(): SelectedCategory? {
        return selectedCategory.value
    }

    @Composable
    private fun getAccountIdFilterList(): ImmutableList<UUID> {
        return accountIdFilterList.value
    }

    @Composable
    private fun getShowCloseButtonOnly(): Boolean {
        return showCloseButtonOnly.value
    }

    @Composable
    private fun getFilterExcluded(): Boolean {
        return filterExcluded.value
    }

    @Composable
    private fun getTransactions(): ImmutableList<Transaction> {
        return transactions.value
    }

    @Composable
    private fun getChoosePeriodModal(): ChoosePeriodModalData? {
        return choosePeriodModal.value
    }

    override fun onEvent(event: PieChartStatisticEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is PieChartStatisticEvent.OnSelectNextMonth -> nextMonth()
                is PieChartStatisticEvent.OnSelectPreviousMonth -> previousMonth()
                is PieChartStatisticEvent.OnSetPeriod -> onSetPeriod(event.timePeriod)
                is PieChartStatisticEvent.OnShowMonthModal -> configureMonthModal(event.timePeriod)
                is PieChartStatisticEvent.OnCategoryClicked -> onCategoryClicked(event.category)
                is PieChartStatisticEvent.OnStart -> start(event.screen)
            }
        }
    }

    private fun start(
        screen: PieChartStatisticScreen
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally(
                period = ivyContext.selectedPeriod,
                type = screen.type,
                accountIdFilterList = screen.accountList,
                filterExclude = screen.filterExcluded,
                transactions = screen.transactions,
                transfersAsIncomeExpenseValue = screen.treatTransfersAsIncomeExpense
            )
        }
    }

    private suspend fun startInternally(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: ImmutableList<UUID>,
        filterExclude: Boolean,
        transactions: ImmutableList<Transaction>,
        transfersAsIncomeExpenseValue: Boolean
    ) {
        initialise(period, type, accountIdFilterList, filterExclude, transactions)
        treatTransfersAsIncomeExpense.value = transfersAsIncomeExpenseValue
        load(periodValue = period)
    }

    private suspend fun initialise(
        periodValue: TimePeriod,
        type: TransactionType,
        accountIdFilterListValue: ImmutableList<UUID>,
        filterExcludedValue: Boolean,
        transactionsValue: ImmutableList<Transaction>
    ) {
        val settings = ioThread { settingsDao.findFirst() }
        val baseCurrencyValue = settings.currency

        period.value = periodValue
        transactionType.value = type
        accountIdFilterList.value = accountIdFilterListValue
        filterExcluded.value = filterExcludedValue
        transactions.value = transactionsValue
        showCloseButtonOnly.value = transactionsValue.isNotEmpty()
        baseCurrency.value = baseCurrencyValue
    }

    private suspend fun load(
        periodValue: TimePeriod
    ) {
        val type = transactionType.value
        val accountIdFilterList = accountIdFilterList.value
        val transactions = transactions.value
        val baseCurrency = baseCurrency.value
        val range = periodValue.toRange(ivyContext.startDayOfMonth)

        val treatTransferAsIncExp =
            sharedPrefs.getBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                false
            ) && accountIdFilterList.isNotEmpty() && treatTransfersAsIncomeExpense.value

        val pieChartActOutput = ioThread {
            pieChartAct(
                PieChartAct.Input(
                    baseCurrency = baseCurrency,
                    range = range,
                    type = type,
                    accountIdFilterList = accountIdFilterList,
                    treatTransferAsIncExp = treatTransferAsIncExp,
                    existingTransactions = transactions,
                    showAccountTransfersCategory = accountIdFilterList.isNotEmpty()
                )
            )
        }

        val totalAmountValue = pieChartActOutput.totalAmount
        val categoryAmountsValue = pieChartActOutput.categoryAmounts

        period.value = periodValue
        totalAmount.doubleValue = totalAmountValue
        categoryAmounts.value = categoryAmountsValue
        selectedCategory.value = null
    }

    private suspend fun onSetPeriod(periodValue: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(periodValue)
        load(
            periodValue = periodValue
        )
    }

    private suspend fun nextMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.ivy.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                periodValue = month.incrementMonthPeriod(ivyContext, 1L, year)
            )
        }
    }

    private suspend fun previousMonth() {
        val month = period.value.month
        val year = period.value.year ?: com.ivy.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                periodValue = month.incrementMonthPeriod(ivyContext, -1L, year)
            )
        }
    }

    private suspend fun configureMonthModal(timePeriod: TimePeriod?) {
        val choosePeriodModalData = if (timePeriod != null) {
            ChoosePeriodModalData(period = timePeriod)
        } else {
            null
        }

        choosePeriodModal.value = choosePeriodModalData
    }

    private suspend fun onCategoryClicked(clickedCategory: Category?) {
        val selectedCategoryValue = if (clickedCategory == selectedCategory.value?.category) {
            null
        } else {
            SelectedCategory(category = clickedCategory)
        }

        val existingCategoryAmounts = categoryAmounts.value
        val newCategoryAmounts = if (selectedCategoryValue != null) {
            existingCategoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedCategoryValue.category == it.category
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.amount
            }
        }.toImmutableList()

        selectedCategory.value = selectedCategoryValue
        categoryAmounts.value = newCategoryAmounts
    }
}