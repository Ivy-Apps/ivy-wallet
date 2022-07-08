package com.ivy.wallet.ui.statistic.level1

import androidx.lifecycle.viewModelScope
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.charts.PieChartAct
import com.ivy.wallet.domain.action.charts.PieChartSubCategoryAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.PieChartStatistic
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import com.ivy.wallet.utils.replace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val pieChartAct: PieChartAct,
    private val pieChartWithSubCategoryAct: PieChartSubCategoryAct,
    private val sharedPrefs: SharedPrefs
) : FRPViewModel<PieChartStatisticState, Nothing>() {

    override val _state: MutableStateFlow<PieChartStatisticState> = MutableStateFlow(
        PieChartStatisticState()
    )

    override suspend fun handleEvent(event: Nothing): suspend () -> PieChartStatisticState {
        TODO("Not yet implemented")
    }

    private val _treatTransfersAsIncomeExpense = MutableStateFlow(false)
    private val treatTransfersAsIncomeExpense = _treatTransfersAsIncomeExpense.readOnly()

    fun start(
        screen: PieChartStatistic
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally(
                period = ivyContext.selectedPeriod,
                type = screen.type,
                accountIdFilterList = screen.accountList,
                filterExclude = screen.filterExcluded,
                transactions = screen.transactions,
                treatTransfersAsIncomeExpense = screen.treatTransfersAsIncomeExpense
            )
        }
    }

    private suspend fun startInternally(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: List<UUID>,
        filterExclude: Boolean,
        transactions: List<Transaction>,
        treatTransfersAsIncomeExpense: Boolean
    ) {
        initialise(period, type, accountIdFilterList, filterExclude, transactions)
        _treatTransfersAsIncomeExpense.value = treatTransfersAsIncomeExpense
        load(period = period)
    }

    private suspend fun initialise(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: List<UUID>,
        filterExclude: Boolean,
        transactions: List<Transaction>
    ) {
        val settings = ioThread { settingsDao.findFirst() }
        val baseCurrency = settings.currency

        updateState {
            it.copy(
                period = period,
                transactionType = type,
                accountIdFilterList = accountIdFilterList,
                filterExcluded = filterExclude,
                transactions = transactions,
                showCloseButtonOnly = transactions.isNotEmpty(),
                baseCurrency = baseCurrency
            )
        }
    }

    private suspend fun load(
        period: TimePeriod
    ) {
        val type = stateVal().transactionType
        val accountIdFilterList = stateVal().accountIdFilterList
        val transactions = stateVal().transactions
        val baseCurrency = stateVal().baseCurrency
        val range = period.toRange(ivyContext.startDayOfMonth)

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

        val totalAmount = pieChartActOutput.totalAmount
        val categoryAmounts = suspend { pieChartActOutput.categoryAmounts } thenInvokeAfter {
            pieChartWithSubCategoryAct(it)
        }

        updateState {
            it.copy(
                period = period,
                totalAmount = totalAmount,
                categoryAmounts = categoryAmounts,
                pieChartCategoryAmount = categoryAmounts,
                selectedCategory = null
            )
        }
    }

    private suspend fun onSetPeriod(period: TimePeriod) {
        ivyContext.updateSelectedPeriodInMemory(period)
        load(
            period = period
        )
    }

    private suspend fun nextMonth() {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year)
            )
        }
    }

    private suspend fun previousMonth() {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year)
            )
        }
    }

    private suspend fun configureMonthModal(timePeriod: TimePeriod?) {
        val choosePeriodModalData = if (timePeriod != null)
            ChoosePeriodModalData(period = timePeriod)
        else
            null

        updateState {
            it.copy(choosePeriodModal = choosePeriodModalData)
        }
    }

    private suspend fun onCategoryClicked(clickedCategory: Category?) {
        val selectedCategory = if (clickedCategory == stateVal().selectedCategory?.category)
            null
        else
            SelectedCategory(category = clickedCategory)

        val categoryToSort =
            if (selectedCategory?.category != null && selectedCategory.category.isSubCategory() && stateVal().categoryAmounts.size != stateVal().pieChartCategoryAmount.size)
                SelectedCategory(findParentCategory(selectedCategory.category))
            else
                selectedCategory

        val existingCategoryAmounts = stateVal().categoryAmounts
        val newCategoryAmounts = if (categoryToSort != null && selectedCategory != null) {
            existingCategoryAmounts
                .sortedByDescending { it.totalAmount() }
                .sortedByDescending {
                    categoryToSort.category == it.category
                }.map {
                    val subList = it.subCategoryState.subCategoriesList
                        .sortedByDescending { sc -> sc.amount }
                        .sortedByDescending { sc -> selectedCategory.category == sc.category }

                    it.copy(subCategoryState = it.subCategoryState.copy(subCategoriesList = subList))
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.totalAmount()
            }
        }

        updateState {
            it.copy(
                selectedCategory = selectedCategory,
                categoryAmounts = newCategoryAmounts
            )
        }
    }

    private suspend fun onSubcategoryListExpand(
        categoryAmount: CategoryAmount,
        expandedState: Boolean
    ) {
        val newCategoryAmount = if (expandedState)
            categoryAmount.copy(
                amount = categoryAmount.amount - categoryAmount.subCategoryState.subCategoryTotalAmount,
                subCategoryState = categoryAmount.subCategoryState.copy(subCategoryListExpanded = expandedState)
            )
        else
            categoryAmount.copy(
                amount = categoryAmount.amount + categoryAmount.subCategoryState.subCategoryTotalAmount,
                subCategoryState = categoryAmount.subCategoryState.copy(subCategoryListExpanded = expandedState)
            )

        updateState {
            it.copy(
                pieChartCategoryAmount = if (expandedState)
                    it.pieChartCategoryAmount.plus(categoryAmount.subCategoryState.subCategoriesList.toSet())
                else
                    it.pieChartCategoryAmount.minus(categoryAmount.subCategoryState.subCategoriesList.toSet()),
                categoryAmounts = it.categoryAmounts.replace(categoryAmount, newCategoryAmount)
            )
        }
    }


    private fun findParentCategory(subCategory: Category): Category? {
        return stateVal().categoryAmounts.find { it.category?.id == subCategory.parentCategoryId }?.category
    }

    fun onEvent(event: PieChartStatisticEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is PieChartStatisticEvent.OnSelectNextMonth -> nextMonth()
                is PieChartStatisticEvent.OnSelectPreviousMonth -> previousMonth()
                is PieChartStatisticEvent.OnSetPeriod -> onSetPeriod(event.timePeriod)
                is PieChartStatisticEvent.OnShowMonthModal -> configureMonthModal(event.timePeriod)
                is PieChartStatisticEvent.OnCategoryClicked -> onCategoryClicked(event.category)
                is PieChartStatisticEvent.OnSubCategoryListExpanded -> onSubcategoryListExpand(
                    event.categoryAmount,
                    event.expandedState
                )
            }
        }
    }
}

data class PieChartStatisticState(
    val transactionType: TransactionType = TransactionType.INCOME,
    val period: TimePeriod = TimePeriod(),
    val baseCurrency: String = "",
    val totalAmount: Double = 0.0,
    val categoryAmounts: List<CategoryAmount> = emptyList(),
    val pieChartCategoryAmount: List<CategoryAmount> = emptyList(),
    val selectedCategory: SelectedCategory? = null,
    val accountIdFilterList: List<UUID> = emptyList(),
    val showCloseButtonOnly: Boolean = false,
    val filterExcluded: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val choosePeriodModal: ChoosePeriodModalData? = null
)

sealed class PieChartStatisticEvent {
    object OnSelectNextMonth : PieChartStatisticEvent()

    object OnSelectPreviousMonth : PieChartStatisticEvent()

    data class OnSetPeriod(val timePeriod: TimePeriod) : PieChartStatisticEvent()

    data class OnCategoryClicked(val category: Category?) :
        PieChartStatisticEvent()

    data class OnShowMonthModal(val timePeriod: TimePeriod?) : PieChartStatisticEvent()

    data class OnSubCategoryListExpanded(
        val categoryAmount: CategoryAmount,
        val expandedState: Boolean
    ) : PieChartStatisticEvent()
}