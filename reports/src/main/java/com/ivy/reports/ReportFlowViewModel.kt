package com.ivy.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ivy.core.action.FlowViewModel
import com.ivy.core.action.calculate.transaction.GroupTrnsFlow
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.time.SelectedPeriodFlow
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.transaction.*
import com.ivy.reports.ReportFilterEvent.SelectAmount
import com.ivy.reports.ReportFilterEvent.SelectAmount.AmountType
import com.ivy.reports.ReportFilterEvent.SelectKeyword
import com.ivy.reports.ReportFilterEvent.SelectKeyword.KeywordsType
import com.ivy.reports.actions.ReportAccountsFlow
import com.ivy.reports.actions.ReportCategoriesFlow
import com.ivy.reports.actions.ReportFilterTrnsFlow
import com.ivy.reports.actions.calculate.CalculateWithTransfersFlow
import com.ivy.reports.data.ReportCategoryType
import com.ivy.reports.data.ReportPlannedPaymenttType
import com.ivy.reports.data.SelectableAccount
import com.ivy.reports.data.SelectableReportsCategory
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.utils.replace
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ReportFlowViewModel @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val reportCategoriesFLow: ReportCategoriesFlow,
    private val reportsAccountsFlow: ReportAccountsFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val calculateWithTransfersFlow: CalculateWithTransfersFlow,
    private val reportFilterTrnsFlow: ReportFilterTrnsFlow,
    private val groupTrnsFlow: GroupTrnsFlow,
    private val exportCSVLogic: ExportCSVLogic,
) : FlowViewModel<ReportState, ReportState, ReportsEvent>() {

    private val filter: MutableStateFlow<FilterState> = MutableStateFlow(emptyFilterState())
    private val filterVisible = MutableStateFlow(false)

    private val loading = MutableStateFlow(false)
    private val trnsList = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        initialiseData()
    }

    override fun initialState(): ReportState = emptyReportScreenState("")

    override fun stateFlow(): Flow<ReportState> = combineMultiple(
        baseCurrencyFlow(), headerStateflow(), grpTrnsListFlow(), filter, filterVisible, loading
    ) { baseCurr, headerState, grpTrnsList, filterState, visible, loading ->

        ReportState(
            baseCurrency = baseCurr,

            headerState = headerState.toImmutableItem(),
            trnsList = grpTrnsList.toImmutableItem(),

            filterVisible = visible,
            filterState = filterState,

            loading = loading
        )
    }

    private fun headerStateflow() = trnsFlow()
        .flatMapLatest { list ->
            baseCurrencyFlow().flatMapMerge { currencyCode ->
                calculateWithTransfersFlow(
                    CalculateWithTransfersFlow.Input(
                        trns = list,
                        outputCurrency = currencyCode,
                        accounts = getSelectedAccounts()
                    )
                )
            }.map { transactionStats ->
                HeaderState(
                    balance = transactionStats.balance,
                    income = transactionStats.income,
                    expenses = transactionStats.expense,

                    incomeTransactionsCount = transactionStats.incomesCount,
                    expenseTransactionsCount = transactionStats.expensesCount,

                    treatTransfersAsIncExp = false,
                    showTransfersAsIncExpCheckbox = false,

                    transactionsOld = list.toOld().toImmutableItem(),
                    accountIdFilters = getSelectedAccounts().map { a -> a.id }
                        .toImmutableItem()
                )
            }
        }
        .onStart { emit(emptyHeaderState()) }

    private fun grpTrnsListFlow() = trnsFlow()
        .flatMapLatest {
            groupTrnsFlow(it)
        }
        .onStart { emptyTransactionList() }

    private fun trnsFlow() = filter.flatMapLatest {
        reportFilterTrnsFlow(it)
    }.onEach { list ->
        trnsList.value = list
    }.onStart { emit(emptyList()) }


    override fun mapToUiState(state: StateFlow<ReportState>): StateFlow<ReportState> = state

    override suspend fun handleEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.FilterOptions -> filterVisible.update { event.visible }

            is ReportsEvent.Export -> {
                export(event.context, event.fileUri, event.onFinish)
            }

            is ReportsEvent.FilterEvent -> {
                handleFilterEvent(event.filterEvent)
            }
        }
    }

    private suspend fun export(context: Context, fileUri: Uri, onFinish: (Uri) -> Unit) {
        if (trnsList.value.isEmpty()) return

        loading.update { true }

        exportCSVLogic.exportToFile(
            context = context,
            fileUri = fileUri,
            exportScope = {
                trnsList.value.toOld()
            }
        )

        uiThread {
            onFinish(fileUri)
        }

        loading.update { false }
    }


    private fun handleFilterEvent(filterEvent: ReportFilterEvent) {
        when (filterEvent) {
            is ReportFilterEvent.SelectTrnsType -> updateTrnsTypeSelection(
                filter.value,
                filterEvent.type,
                filterEvent.checked
            )

            is ReportFilterEvent.SelectPeriod -> updatePeriodSelection(
                filter.value,
                timePeriod = filterEvent.timePeriod
            )

            is ReportFilterEvent.SelectAccount -> updateAccountSelection(
                filter.value,
                account = filterEvent.account,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectCategory -> updateCategorySelection(
                filter.value,
                category = filterEvent.category,
                add = filterEvent.add
            )

            is SelectAmount -> updateAmountSelection(
                filter.value,
                amt = filterEvent.amt,
                amountType = filterEvent.amountType
            )

            is SelectKeyword -> updateIncludeExcludeKeywords(
                filter.value,
                keyword = filterEvent.keyword,
                keywordsType = filterEvent.keywordsType,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectPlannedPayment -> updatePlannedPaymentSelection(
                filter.value,
                type = filterEvent.type,
                add = filterEvent.add
            )

            is ReportFilterEvent.Clear -> onFilterClear(
                filter.value,
                event = filterEvent
            )

            is ReportFilterEvent.SelectAll -> onSelectAll(
                filter.value,
                event = filterEvent
            )
        }
    }

    private fun updateTrnsTypeSelection(
        filter: FilterState,
        type: TrnType,
        add: Boolean
    ) {
        val types = filter.selectedTrnTypes.data.addOrRemove(add = add, type)
        updateFilter(newFilter = filter.copy(selectedTrnTypes = types.toImmutableItem()))
    }

    private fun updatePeriodSelection(
        filter: FilterState,
        timePeriod: TimePeriod
    ) {
        updateFilter(filter.copy(period = timePeriod.toImmutableItem()))
    }

    private fun updateAccountSelection(
        filter: FilterState,
        account: SelectableAccount,
        add: Boolean
    ) {

        val newAccount = account.copy(selected = !account.selected)
        val selectableAccounts = filter.selectedAcc.data.replace(oldComp = { a ->
            a.account.id == newAccount.account.id
        }, newAccount)

        updateFilter(
            filter.copy(
                selectedAcc = selectableAccounts.toImmutableItem()
            )
        )
    }

    private fun updateCategorySelection(
        filter: FilterState,
        category: SelectableReportsCategory,
        add: Boolean
    ) {

        val newCategory = category.copy(selected = !category.selected)
        val allCats = filter.selectedCat.data.replace(oldComp = { c ->
            when {
                (newCategory.selectableCategory is ReportCategoryType.Cat) && (c.selectableCategory is ReportCategoryType.Cat) && c.selectableCategory.cat.id == newCategory.selectableCategory.cat.id -> true
                newCategory.selectableCategory is ReportCategoryType.None && c.selectableCategory is ReportCategoryType.None -> true
                else -> false
            }
        }, newCategory)

        updateFilter(
            newFilter = filter.copy(
                selectedCat = allCats.toImmutableItem()
            )
        )
    }

    private fun updateAmountSelection(
        filter: FilterState,
        amt: Double?,
        amountType: AmountType
    ) {
        val newFilter = when (amountType) {
            AmountType.MIN -> filter.copy(minAmount = amt)
            AmountType.MAX -> filter.copy(maxAmount = amt)
        }

        updateFilter(newFilter = newFilter)
    }

    private fun updateIncludeExcludeKeywords(
        filter: FilterState,
        keyword: String,
        keywordsType: KeywordsType,
        add: Boolean
    ) {
        val trimmedKeyword = keyword.trim()

        val existingKeywords = when (keywordsType) {
            KeywordsType.INCLUDE -> filter.includeKeywords.data
            KeywordsType.EXCLUDE -> filter.excludeKeywords.data
        }

        val updatedKeywords = if (add && trimmedKeyword !in existingKeywords)
            existingKeywords.addOrRemove(add = true, trimmedKeyword)
        else if (!add)
            existingKeywords.addOrRemove(add = false, trimmedKeyword)
        else
            existingKeywords


        val newFilter = when (keywordsType) {
            KeywordsType.INCLUDE -> filter.copy(includeKeywords = updatedKeywords.toImmutableItem())
            KeywordsType.EXCLUDE -> filter.copy(excludeKeywords = updatedKeywords.toImmutableItem())
        }

        updateFilter(newFilter = newFilter)
    }

    private fun updatePlannedPaymentSelection(
        filter: FilterState,
        type: ReportPlannedPaymenttType,
        add: Boolean
    ) {
        val selectPlannedPayments =
            filter.selectedPlannedPayments.data.addOrRemove(add = add, item = type)
                .toImmutableItem()

        updateFilter(newFilter = filter.copy(selectedPlannedPayments = selectPlannedPayments))
    }

    private fun onFilterClear(
        filter: FilterState,
        event: ReportFilterEvent.Clear
    ) {
        val newFilter = when (event) {
            is ReportFilterEvent.Clear.Filter -> emptyFilterState()
            ReportFilterEvent.Clear.Accounts -> filter.copy(selectedAcc = filter.selectedAcc.data.map { a ->
                a.copy(
                    selected = false
                )
            }.toImmutableItem())
            ReportFilterEvent.Clear.Categories -> filter.copy(selectedCat = filter.selectedCat.data.map {
                it.copy(
                    selected = false
                )
            }.toImmutableItem())
        }
        updateFilter(newFilter)
    }


    private fun onSelectAll(
        filter: FilterState,
        event: ReportFilterEvent.SelectAll
    ) {
        val newFilter = when (event) {
            is ReportFilterEvent.SelectAll.Accounts -> filter.copy(
                selectedAcc = filter.selectedAcc.data.map { a -> a.copy(selected = true) }
                    .toImmutableItem()
            )
            ReportFilterEvent.SelectAll.Categories -> filter.copy(
                selectedCat = filter.selectedCat.data.map { c -> c.copy(selected = true) }
                    .toImmutableItem()
            )
        }

        updateFilter(newFilter = newFilter)
    }

    private fun updateFilter(newFilter: FilterState) {
        filter.value = newFilter
    }

    private fun initialiseData() {
        combine(reportsAccountsFlow(Unit), reportCategoriesFLow(Unit)) { a, c ->
            Pair(a, c)
        }.onEach { pair ->
            filter.update {
                it.copy(
                    selectedAcc = pair.first.toImmutableItem(),
                    selectedCat = pair.second.toImmutableItem()
                )
            }
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }


    private fun getSelectedAccounts() =
        filter.value.selectedAcc.data.filter { it.selected }.map { it.account }

    private fun <T> List<T>.addOrRemove(add: Boolean, item: T): List<T> {
        return if (add)
            this + item
        else
            this - item
    }


    private fun List<Transaction>.toOld(): List<TransactionOld> {
        return this.map { newTrans ->

            val toOldTrans = TransactionOld(
                accountId = newTrans.account.id,
                type = TrnType.INCOME,
                amount = newTrans.value.amount.toBigDecimal(),
                title = newTrans.title,
                description = newTrans.description,
                dateTime = (newTrans.time as? TrnTime.Actual)?.actual,
                categoryId = newTrans.category?.id,
                dueDate = (newTrans.time as? TrnTime.Due)?.due,
                recurringRuleId = newTrans.metadata.recurringRuleId,
                loanId = newTrans.metadata.loanId,
                loanRecordId = newTrans.metadata.loanRecordId,
                id = newTrans.id,
            )

            when (newTrans.type) {
                TransactionType.Income -> toOldTrans.copy(type = TrnType.INCOME)
                TransactionType.Expense -> toOldTrans.copy(type = TrnType.EXPENSE)
                else -> toOldTrans.copy(
                    type = TrnType.TRANSFER,
                    toAmount = (newTrans.type as TransactionType.Transfer).toValue.amount.toBigDecimal(),
                    toAccountId = (newTrans.type as TransactionType.Transfer).toAccount.id
                )
            }
        }
    }
}