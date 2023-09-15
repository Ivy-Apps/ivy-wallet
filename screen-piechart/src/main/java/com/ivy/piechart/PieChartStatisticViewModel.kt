package com.ivy.piechart

import androidx.lifecycle.viewModelScope
import com.ivy.legacy.data.SharedPrefs
import com.ivy.core.data.db.entity.TransactionType
import com.ivy.core.data.model.Category
import com.ivy.core.data.model.Transaction
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.readOnly
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.piechart.action.PieChartAct
import com.ivy.core.data.db.dao.SettingsDao
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PieChartStatisticViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val pieChartAct: PieChartAct,
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
        screen: PieChartStatisticScreen
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
        accountIdFilterList: ImmutableList<UUID>,
        filterExclude: Boolean,
        transactions: ImmutableList<Transaction>,
        treatTransfersAsIncomeExpense: Boolean
    ) {
        initialise(period, type, accountIdFilterList, filterExclude, transactions)
        _treatTransfersAsIncomeExpense.value = treatTransfersAsIncomeExpense
        load(period = period)
    }

    private suspend fun initialise(
        period: TimePeriod,
        type: TransactionType,
        accountIdFilterList: ImmutableList<UUID>,
        filterExclude: Boolean,
        transactions: ImmutableList<Transaction>
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
        val categoryAmounts = pieChartActOutput.categoryAmounts

        updateState {
            it.copy(
                period = period,
                totalAmount = totalAmount,
                categoryAmounts = categoryAmounts,
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
        val year = stateVal().period.year ?: com.ivy.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, 1L, year)
            )
        }
    }

    private suspend fun previousMonth() {
        val month = stateVal().period.month
        val year = stateVal().period.year ?: com.ivy.legacy.utils.dateNowUTC().year
        if (month != null) {
            load(
                period = month.incrementMonthPeriod(ivyContext, -1L, year)
            )
        }
    }

    private suspend fun configureMonthModal(timePeriod: TimePeriod?) {
        val choosePeriodModalData = if (timePeriod != null) {
            ChoosePeriodModalData(period = timePeriod)
        } else {
            null
        }

        updateState {
            it.copy(choosePeriodModal = choosePeriodModalData)
        }
    }

    private suspend fun onCategoryClicked(clickedCategory: Category?) {
        val selectedCategory = if (clickedCategory == stateVal().selectedCategory?.category) {
            null
        } else {
            SelectedCategory(category = clickedCategory)
        }

        val existingCategoryAmounts = stateVal().categoryAmounts
        val newCategoryAmounts = if (selectedCategory != null) {
            existingCategoryAmounts
                .sortedByDescending { it.amount }
                .sortedByDescending {
                    selectedCategory.category == it.category
                }
        } else {
            existingCategoryAmounts.sortedByDescending {
                it.amount
            }
        }.toImmutableList()

        updateState {
            it.copy(
                selectedCategory = selectedCategory,
                categoryAmounts = newCategoryAmounts
            )
        }
    }

    fun onEvent(event: PieChartStatisticEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is PieChartStatisticEvent.OnSelectNextMonth -> nextMonth()
                is PieChartStatisticEvent.OnSelectPreviousMonth -> previousMonth()
                is PieChartStatisticEvent.OnSetPeriod -> onSetPeriod(event.timePeriod)
                is PieChartStatisticEvent.OnShowMonthModal -> configureMonthModal(event.timePeriod)
                is PieChartStatisticEvent.OnCategoryClicked -> onCategoryClicked(event.category)
            }
        }
    }
}

data class PieChartStatisticState(
    val transactionType: TransactionType = TransactionType.INCOME,
    val period: TimePeriod = TimePeriod(),
    val baseCurrency: String = "",
    val totalAmount: Double = 0.0,
    val categoryAmounts: ImmutableList<CategoryAmount> = persistentListOf(),
    val selectedCategory: SelectedCategory? = null,
    val accountIdFilterList: ImmutableList<UUID> = persistentListOf(),
    val showCloseButtonOnly: Boolean = false,
    val filterExcluded: Boolean = false,
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val choosePeriodModal: ChoosePeriodModalData? = null
)

sealed class PieChartStatisticEvent {
    object OnSelectNextMonth : PieChartStatisticEvent()

    object OnSelectPreviousMonth : PieChartStatisticEvent()

    data class OnSetPeriod(val timePeriod: TimePeriod) : PieChartStatisticEvent()

    data class OnCategoryClicked(val category: Category?) :
        PieChartStatisticEvent()

    data class OnShowMonthModal(val timePeriod: TimePeriod?) : PieChartStatisticEvent()
}
