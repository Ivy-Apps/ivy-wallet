package com.ivy.wallet.ui.statistic.level1

import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.domain.action.charts.PieChartAct
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
import com.ivy.wallet.utils.emptyImmutableList
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.readOnly
import com.ivy.wallet.utils.toActualImmutableList
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
        accountIdFilterImmutableList: ImmutableList<UUID>,
        filterExclude: Boolean,
        transactions: ImmutableList<Transaction>
    ) {
        val settings = ioThread { settingsDao.findFirst() }
        val baseCurrency = settings.currency

        updateState {
            it.copy(
                period = period,
                transactionType = type,
                accountIdFilterList = accountIdFilterImmutableList,
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
        val accountIdFilterImmutableList = stateVal().accountIdFilterList
        val transactions = stateVal().transactions
        val baseCurrency = stateVal().baseCurrency
        val range = period.toRange(ivyContext.startDayOfMonth)

        val treatTransferAsIncExp =
            sharedPrefs.getBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                false
            ) && accountIdFilterImmutableList.isNotEmpty() && treatTransfersAsIncomeExpense.value

        val pieChartActOutput = ioThread {
            pieChartAct(
                PieChartAct.Input(
                    baseCurrency = baseCurrency,
                    range = range,
                    type = type,
                    accountIdFilterList = accountIdFilterImmutableList,
                    treatTransferAsIncExp = treatTransferAsIncExp,
                    existingTransactions = transactions,
                    showAccountTransfersCategory = accountIdFilterImmutableList.isNotEmpty()
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
        }

        updateState {
            it.copy(
                selectedCategory = selectedCategory,
                categoryAmounts = newCategoryAmounts.toActualImmutableList()
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
    val categoryAmounts: ImmutableList<CategoryAmount> = emptyImmutableList(),
    val selectedCategory: SelectedCategory? = null,
    val accountIdFilterList: ImmutableList<UUID> = emptyImmutableList(),
    val showCloseButtonOnly: Boolean = false,
    val filterExcluded: Boolean = false,
    val transactions: ImmutableList<Transaction> = emptyImmutableList(),
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
