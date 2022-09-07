package com.ivy.reports

import androidx.compose.ui.graphics.toArgb
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.ivy.base.R
import com.ivy.common.atEndOfDay
import com.ivy.common.timeNowUTC
import com.ivy.core.action.FlowViewModel
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.calculate.CalculateWithTransfersAct
import com.ivy.core.action.calculate.transaction.GroupTrnsAct
import com.ivy.core.action.category.CategoriesFlow
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.currency.exchange.ExchangeAct
import com.ivy.core.action.time.SelectedPeriodFlow
import com.ivy.core.action.transaction.TrnsAct
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.functions.transaction.*
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.time.Period
import com.ivy.data.transaction.*
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.reports.ReportFilterEvent.SelectAmount
import com.ivy.reports.ReportFilterEvent.SelectAmount.AmountType
import com.ivy.reports.ReportFilterEvent.SelectKeyword
import com.ivy.reports.ReportFilterEvent.SelectKeyword.KeywordsType
import com.ivy.reports.actions.ReportsDataFlow
import com.ivy.reports.data.PlannedPaymentTypes
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.utils.replace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class ReportFlowViewModel @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val categoryFlow: CategoriesFlow,
    private val accountsFLow: AccountsFlow,
    private val queryTrnsAct: TrnsAct,
    private val groupTrnsAct: GroupTrnsAct,
    private val exchangeAct: ExchangeAct,
    private val calculateAct: CalculateWithTransfersAct,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val headerStateFlow: ReportsDataFlow
) : FlowViewModel<ReportState, ReportState, ReportsEvent>() {
    override fun initialState(): ReportState =
        emptyReportScreenState("")

    private val filter = MutableStateFlow(emptyFilterState())
    private val filterVisible = MutableStateFlow(false)

    private val a = AtomicInteger(0)
    private val _categoryNone = dummyCategory(
        name = "None",
        color = Gray.toArgb(),
        icon = dummyIconSized(R.drawable.ic_custom_category_s)
    )

    override fun stateFlow(): Flow<ReportState> = combine(
        baseCurrencyFlow(), reportFilterStateFlow(), headerStateFlow(), filterVisible
    ) { baseCurr, filterState, dataHolder, visible ->
        emptyReportScreenState(baseCurr).copy(
            filterState = filterState,
            headerState = dataHolder.headerState,
            filterVisible = visible,
            trnsList = dataHolder.trnsList.toImmutableItem()
        )
    }


    override fun mapToUiState(state: StateFlow<ReportState>): StateFlow<ReportState> = state

    override suspend fun handleEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.FilterOptions -> filterVisible.update { event.visible }
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
                selectedAcc = filter.allAccounts.data.toList().toImmutableItem()
            )
            ReportFilterEvent.SelectAll.Categories -> filter.copy(
                selectedCat = filter.allCategories.data.toList().toImmutableItem()
            )
        }

        updateFilter(newFilter = newFilter)
    }

    private fun onFilterClear(
        filter: FilterState,
        event: ReportFilterEvent.Clear
    ) {
        val newFilter = when (event) {
            is ReportFilterEvent.Clear.Filter -> emptyFilterState()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun reportFilterStateFlow(): Flow<FilterState> = filter.flatMapLatest { ogFilter ->
        if (ogFilter.allAccounts.data.isEmpty() || ogFilter.allCategories.data.isEmpty()) {
            combine(accountsFLow(), categoryFlow()) { a, c ->
                filter.updateAndGet {
                    it.copy(
                        allCategories = c.toList().toImmutableItem(),
                        allAccounts = a.toList().toImmutableItem()
                    )
                }
            }
        } else
            flowOf(ogFilter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun headerStateFlow() = reportFilterStateFlow().flatMapLatest {
        headerStateFlow(it)
    }.onStart { emit(ReportsDataFlow.DataHolder.empty()) }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private fun headerStateFlow() = reportFilterStateFlow().flatMapLatest {
//        flowOf(it)
//            .debounce(300)
//            .filter { fil -> fil.isValid() }
//            .flatMapMerge { ogFil ->
//                headerStateFlow(ogFil)
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

    private suspend fun filterTransactions(
        baseCurrency: CurrencyCode,
        filter: FilterState
    ) = {
        TrnWhere.ByTypeIn(filter.selectedTrnTypes.data.toNonEmptyList()) and
                ByDate(filter.period.data) and
                ByAccount(filter.selectedAcc.data) and
                TrnWhere.ByCategoryIn(filter.selectedCat.data.noneCategoryFix().toNonEmptyList())
    } then queryTrnsAct then {
        filterByAmount(
            baseCurrency = baseCurrency,
            minAmt = filter.minAmount,
            maxAmt = filter.maxAmount,
            transList = it
        )
    } then {
        filterByWords(
            includeKeywords = filter.includeKeywords.data,
            excludeKeywords = filter.excludeKeywords.data,
            transactionsList = it
        )
    } thenInvokeAfter { allTrans ->

        val actualTrns =
            getActualTrns(
                selectedPlannedPayments = filter.selectedPlannedPayments.data,
                allTrns = allTrans
            )

        val stats = calculateAct(
            CalculateWithTransfersAct.Input(
                trns = actualTrns,
                outputCurrency = baseCurrency,
                accounts = filter.selectedAcc.data
            )
        )

        Pair(stats, allTrans)
    }

    private fun getActualTrns(
        selectedPlannedPayments: List<PlannedPaymentTypes>,
        allTrns: List<Transaction>
    ): List<Transaction> {
        return if (selectedPlannedPayments.isEmpty())
            allTrns.filter(::actual)
        else if (selectedPlannedPayments.contains(PlannedPaymentTypes.UPCOMING)
            && selectedPlannedPayments.contains(PlannedPaymentTypes.OVERDUE)
        )
            allTrns
        else {
            val timeNow = timeNowUTC()
            val type = selectedPlannedPayments.first()
            allTrns.filter {
                when (it.time) {
                    is TrnTime.Due -> {
                        when (type) {
                            PlannedPaymentTypes.UPCOMING -> upcoming(it, timeNow)
                            PlannedPaymentTypes.OVERDUE -> overdue(it, timeNow)
                        }
                    }
                    else -> true
                }
            }
        }
    }

    @Suppress("FunctionName")
    private fun ByDate(timePeriod: TimePeriod?): TrnWhere {
        val datePeriod = timePeriod!!.toPeriodDate(1)

        return brackets(TrnWhere.ActualBetween(datePeriod) or TrnWhere.DueBetween(datePeriod))
    }

    @Suppress("FunctionName")
    private fun ByAccount(accounts: List<Account>): TrnWhere {
        val nonEmptyList = accounts.toNonEmptyList()
        return brackets(TrnWhere.ByAccountIn(nonEmptyList) or TrnWhere.ByToAccountIn(nonEmptyList))
    }

    private suspend fun filterByAmount(
        baseCurrency: CurrencyCode,
        minAmt: Double?,
        maxAmt: Double?,
        transList: List<Transaction>
    ): List<Transaction> {
        suspend fun amountInBaseCurrency(
            amount: Double?,
            toCurr: CurrencyCode?,
            baseCur: CurrencyCode = baseCurrency,
        ): Double {
            amount ?: return 0.0

            return exchangeAct(
                ExchangeAct.Input(
                    from = baseCur,
                    to = toCurr ?: baseCur,
                    amount = amount
                )
            ).getOrElse { amount }
        }

        suspend fun filterTrans(
            transactionList: List<Transaction> = transList,
            filterAmount: (Double) -> Boolean
        ): List<Transaction> {
            return transactionList.filter {
                val amt =
                    amountInBaseCurrency(amount = it.value.amount, toCurr = it.account.currency)

                val transferTrnsValue = (it.type as? TransactionType.Transfer)?.toValue

                val toAmt = amountInBaseCurrency(
                    transferTrnsValue?.amount,
                    toCurr = transferTrnsValue?.currency
                )

                filterAmount(amt) || filterAmount(toAmt)
            }
        }

        return when {
            minAmt != null && maxAmt != null ->
                filterTrans { amt -> amt >= minAmt && amt <= maxAmt }
            minAmt != null -> filterTrans { amt -> amt >= minAmt }
            maxAmt != null -> filterTrans { amt -> amt <= maxAmt }
            else -> {
                transList
            }
        }
    }

    private fun filterByWords(
        includeKeywords: List<String>,
        excludeKeywords: List<String>,
        transactionsList: List<Transaction>
    ): List<Transaction> {
        fun List<Transaction>.filterTrans(
            keyWords: List<String>,
            include: Boolean = true
        ): List<Transaction> {
            if (keyWords.isEmpty())
                return this

            return this.filter {
                val title = it.title ?: ""
                val description = it.description ?: ""

                keyWords.forEach { k ->
                    val key = k.trim()
                    if (title.contains(key, ignoreCase = true) ||
                        description.contains(key, ignoreCase = true)
                    )
                        return@filter include
                }

                !include
            }
        }

        return transactionsList
            .filterTrans(includeKeywords)
            .filterTrans(excludeKeywords, include = false)
    }

    private fun TimePeriod.toPeriodDate(startDateOfMonth: Int): Period {
        val range = toRange(startDateOfMonth)
        val from = range.from().toLocalDate().atStartOfDay()
        val to = range.to().toLocalDate().atEndOfDay()

        return Period.FromTo(from = from, to = to)
    }

    private fun <T> List<T>.toNonEmptyList() = NonEmptyList.fromListUnsafe(this)

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

    private fun List<Category>.noneCategoryFix() = this.replace(_categoryNone, null)
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