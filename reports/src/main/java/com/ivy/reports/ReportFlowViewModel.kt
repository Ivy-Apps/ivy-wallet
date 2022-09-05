package com.ivy.reports

import android.util.Log
import com.ivy.core.action.FlowViewModel
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.category.CategoriesFlow
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.time.SelectedPeriodFlow
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import com.ivy.reports.ReportFilterEvent.SelectAmount
import com.ivy.reports.ReportFilterEvent.SelectAmount.AmountType
import com.ivy.reports.ReportFilterEvent.SelectKeyword
import com.ivy.reports.ReportFilterEvent.SelectKeyword.KeywordsType
import com.ivy.reports.data.PlannedPaymentTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class ReportFlowViewModel @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val categoryFlow: CategoriesFlow,
    private val accountsFLow: AccountsFlow,
    private val selectedPeriodFlow: SelectedPeriodFlow,
) : FlowViewModel<ReportState, ReportState, ReportsEvent>() {
    override fun initialState(): ReportState =
        emptyReportScreenState("")

    private val filter = MutableStateFlow(emptyFilterState())
    private val a = AtomicInteger(0)

    override fun stateFlow(): Flow<ReportState> = combine(
        baseCurrencyFlow(), reportFilterStateFlow(), headerStateFlow()
    ) { baseCurr, filterState, headerState ->
        emptyReportScreenState(baseCurr).copy(filterState = filterState, headerState = headerState)
    }


    override fun mapToUiState(state: StateFlow<ReportState>): StateFlow<ReportState> = state

    override suspend fun handleEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.FilterOptions -> setFilterOptionsVisibility(
                filter.value,
                visible = event.visible
            )
            is ReportsEvent.FilterEvent -> {
                handleFilterEvent(event.filterEvent)
            }
            else -> {

            }
        }
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

            else -> {
            }
        }
    }

    private fun onSelectAll(
        filter: FilterState,
        event: ReportFilterEvent.SelectAll
    ) {
        val newFilter = when (event) {
            is ReportFilterEvent.SelectAll.Accounts -> filter.copy(
                selectedAcc = uiState.value.filterState.allAccounts.data.toList().toImmutableItem()
            )
            ReportFilterEvent.SelectAll.Categories -> filter.copy(
                selectedCat = uiState.value.filterState.allCategories.data.toList()
                    .toImmutableItem()
            )
        }

        updateFilter(newFilter = newFilter)
    }

    private fun onFilterClear(
        filter: FilterState,
        event: ReportFilterEvent.Clear
    ) {
        val newFilter = when (event) {
            is ReportFilterEvent.Clear.Filter -> emptyFilterState().copy(
                visible = true,
            )
            ReportFilterEvent.Clear.Accounts -> filter.copy(selectedAcc = emptyList<Account>().toImmutableItem())
            ReportFilterEvent.Clear.Categories -> filter.copy(selectedCat = emptyList<Category>().toImmutableItem())
        }
        updateFilter(newFilter)
    }

    private fun updatePlannedPaymentSelection(
        filter: FilterState,
        type: PlannedPaymentTypes,
        add: Boolean
    ) {
        val selectPlannedPayments =
            filter.selectedPlannedPayments.data.addOrRemove(add = add, item = type)
                .toImmutableItem()

        updateFilter(newFilter = filter.copy(selectedPlannedPayments = selectPlannedPayments))
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


    private fun updateCategorySelection(
        filter: FilterState,
        category: Category,
        add: Boolean
    ) {
        updateFilter(
            newFilter = filter.copy(
                selectedCat = filter.selectedCat.data.addOrRemove(
                    add = add,
                    item = category
                ).toImmutableItem()
            )
        )
    }


    private fun updatePeriodSelection(
        filter: FilterState,
        timePeriod: TimePeriod
    ) {
        updateFilter(filter.copy(period = timePeriod.toImmutableItem()))
    }

    private fun updateTrnsTypeSelection(
        filter: FilterState,
        type: TrnType,
        add: Boolean
    ) {
        val types = filter.selectedTrnTypes.data.addOrRemove(add = add, type)
        updateFilter(newFilter = filter.copy(selectedTrnTypes = types.toImmutableItem()))
    }

    private fun setFilterOptionsVisibility(filter: FilterState, visible: Boolean) {
        updateFilter(filter.copy(visible = visible))
    }

//    private fun reportFilterStateFlow() =
//        combine(
//            accountsFLow(),
//            categoryFlow(),
//            filter
//        ) { allAcc, allCat, filter ->
//
//            filter.copy(
//                allAccounts = allAcc.toImmutableItem(),
//                allCategories = allCat.toImmutableItem()
//            )
//        }

    private fun reportFilterStateFlow() = filter


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun headerStateFlow() = filter.debounce(5000L).flatMapLatest { it ->
        delay(5000L)
        Log.d("ReportsUIGGGG", ""+a.incrementAndGet())
        flowOf(emptyHeaderState().copy(balance = 10000.0))
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private fun headerStateFlow() = reportFilterStateFlow().flatMapLatest {
//        flowOf(it)
//            .debounce(300L)
//            .filter { x -> x.isValid() && !x.hasEmptyContents() }
//            .map {
//                delay(5000L) //Complex Work
//                emptyHeaderState().copy(balance = 10000.0)
//            }
//            .onStart { emit(state.value.headerState) }
//    }

    private fun FilterState.isValid() = when {
        selectedTrnTypes.data.isEmpty() -> false

        period.data == null -> false

        selectedAcc.data.isEmpty() -> false

        selectedCat.data.isEmpty() -> false

        minAmount != null && maxAmount != null -> when {
            minAmount > maxAmount -> false
            maxAmount < minAmount -> false
            else -> true
        }

        else -> true
    }

    private fun FilterState.hasEmptyContents() =
        this.selectedTrnTypes.data.isEmpty() && period.data == null &&
                selectedAcc.data.isEmpty() && selectedCat.data.isEmpty() &&
                minAmount == null && maxAmount == null &&
                includeKeywords.data.isEmpty() && excludeKeywords.data.isEmpty()


    private fun updateAccountSelection(
        filter: FilterState,
        account: Account,
        add: Boolean
    ) {
        updateFilter(
            filter.copy(
                selectedAcc = filter.selectedAcc.data.addOrRemove(
                    add = add,
                    item = account
                ).toImmutableItem()
            )
        )
    }

    private fun <T> List<T>.addOrRemove(add: Boolean, item: T): List<T> {
        return if (add)
            this + item
        else
            this - item
    }


    private fun updateFilter(newFilter: FilterState) {
        filter.value = newFilter
    }
}

//    private val showFilterOptions = MutableStateFlow(false)
//    private val selectedTrnsTypes = MutableStateFlow<List<TrnType>>(emptyList())
//    private val selectedPeriod = MutableStateFlow<TimePeriod?>(null)
//
//    private val allAccounts = MutableStateFlow<List<Account>>(emptyList())
//    private val selectedAccounts = MutableStateFlow<List<Account>>(emptyList())
//
//    private val allCat = MutableStateFlow<List<Category>>(emptyList())
//    private val selectedCategories = MutableStateFlow<List<Category>>(emptyList())
//
//    private val minAmount = MutableStateFlow<Double?>(null)
//    private val maxAmount = MutableStateFlow<Double?>(null)
//
//    private val includedKeywords = MutableStateFlow<List<String>>(emptyList())
//    private val excludedKeywords = MutableStateFlow<List<String>>(emptyList())
//
//    private val selectedPlannedPayments = MutableStateFlow<List<PlannedPaymentTypes>>(emptyList())

//    override fun stateFlow(): Flow<ReportState> = combine(
//        baseCurrencyFlow(), reportFilterStateFlow()
//    ) { baseCurr, filterState ->
//        emptyReportScreenState(baseCurr).copy(filterState = filterState)
//    }