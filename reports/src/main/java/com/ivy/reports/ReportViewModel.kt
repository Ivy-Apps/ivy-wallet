package com.ivy.reports

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.ivy.base.R
import com.ivy.common.atEndOfDay
import com.ivy.common.timeNowUTC
import com.ivy.core.action.account.AccountsAct
import com.ivy.core.action.calculate.CalculateWithTransfersAct
import com.ivy.core.action.calculate.ExtendedStats
import com.ivy.core.action.calculate.transaction.GroupTrnsAct
import com.ivy.core.action.category.CategoriesAct
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.action.currency.exchange.ExchangeAct
import com.ivy.core.action.transaction.TrnsAct
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.getDefaultCurrencyCode
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.functions.transaction.*
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.*
import com.ivy.frp.lambda
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.reports.ReportFilterEvent.SelectAmount.AmountType
import com.ivy.reports.ReportFilterEvent.SelectKeyword.KeywordsType
import com.ivy.reports.data.PlannedPaymentTypes
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.utils.replace
import com.ivy.wallet.utils.scopedIOThread
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val queryTrnsAct: TrnsAct,
    private val calculateAct: CalculateWithTransfersAct,
    private val exchangeAct: ExchangeAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val groupTrnsAct: GroupTrnsAct,
    private val exportCSVLogic: ExportCSVLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
) : FRPViewModel<ReportState, ReportsEvent>() {
    override val _state: MutableStateFlow<ReportState> = MutableStateFlow(
        emptyReportScreenState(baseCurrency = getDefaultCurrencyCode())
    )

    // "_" (Underscore Prefix) for all global variables
    private var _baseCurrency: CurrencyCode = getDefaultCurrencyCode()
    private var _filter: FilterState = emptyFilterState()

    private var _allAccounts: List<Account> = emptyList()
    private var _allCategories: List<Category> = emptyList()

    private var _trnsStats: ExtendedStats = ExtendedStats.empty()
    private var _allTrns: List<Transaction> = emptyList()

    private val _categoryNone = dummyCategory(
        name = "None",
        color = Gray.toArgb(),
        icon = dummyIconSized(R.drawable.ic_custom_category_s)
    )

    override suspend fun handleEvent(event: ReportsEvent): suspend () -> ReportState =
        withContext(Dispatchers.Default) {
            when (event) {
                is ReportsEvent.Start -> initialiseData()
                is ReportsEvent.FilterOptions -> setFilterOptionsVisibility(event.visible)
                is ReportsEvent.TrnsAsIncomeExpense -> transfersAsIncomeExpense(event.trnsAsIncExp)
                is ReportsEvent.Export -> {
                    export(event.context, event.fileUri, event.onFinish)
                    stateVal().lambda()
                }
                is ReportsEvent.FilterEvent -> {
                    handleFilterEvent(event.filterEvent)
                }
            }
        }

    private fun initialiseData() = suspend {
        _baseCurrency = baseCurrencyAct(Unit)
        _allAccounts = accountsAct(Unit)
        _allCategories = listOf(_categoryNone) + categoriesAct(Unit)

//        //Copy existing filter object with the new baseCurrency to preserve filterOptions state,
//        _filter = _filter.copy()

        updateState {
            it.copy(
                baseCurrency = _baseCurrency,
                filterState = _filter.copy(
                    allAccounts = _allAccounts.toImmutableItem(),
                    allCategories = _allCategories.toImmutableItem(),
                )
            )
        }
    }

    private suspend fun setFilterOptionsVisibility(visible: Boolean) = updateState {
        it.copy(
            filterState = it.filterState.copy(visible = visible),
        )
    }.lambda()

    private suspend fun setFilter(filter: FilterState): suspend () -> ReportState {
        return scopedIOThread { scope ->
            //clear filter
            if (filter.hasEmptyContents())
                return@scopedIOThread clearReportFilter()

            //Report filter Validation
            if (!filter.validateFilter()) return@scopedIOThread stateVal().lambda()

            updateState {
                it.copy(loading = true)
            }

            val (transactionStats, transactions) =
                filterTransactions(baseCurrency = _baseCurrency, filter = filter)

            updateGlobalData(transactionStats, transactions, filter.copy(visible = false))

            val trnsList = groupTrnsAct(transactions)

            val headerState = HeaderState(
                balance = transactionStats.balance,
                income = transactionStats.income,
                expenses = transactionStats.expense,

                incomeTransactionsCount = transactionStats.incomesCount,
                expenseTransactionsCount = transactionStats.expensesCount,

                treatTransfersAsIncExp = false,
                showTransfersAsIncExpCheckbox = showTransfersAsIncExpOption(),

                transactionsOld = _allTrns.toOld(),
                accountIdFilters = filter.selectedAcc.data.map { a -> a.id }
            )

            updateState {
                it.copy(
                    filterState = filter.copy(visible = false),
                    baseCurrency = _baseCurrency,

                    trnsList = trnsList.toImmutableItem(),

                    loading = false,

                    headerState = headerState
                )
            }.lambda()
        }
    }

    private fun FilterState.hasEmptyContents() =
        this.selectedTrnTypes.data.isEmpty() && period.data == null &&
                selectedAcc.data.isEmpty() && selectedCat.data.isEmpty() &&
                minAmount == null && maxAmount == null &&
                includeKeywords.data.isEmpty() && excludeKeywords.data.isEmpty()

    private fun updateGlobalData(
        transactionStats: ExtendedStats,
        transactions: List<Transaction>,
        filter: FilterState
    ) {
        _trnsStats = transactionStats
        _allTrns = transactions
        _filter = filter
    }

    /** Show Transfers As Income/Expense option if and only if
     *  a) User has selected Transaction.Type == TransactionType.Transfer and
     *  b) There are actual transfer transactions in the queried transactions
     */
    private fun showTransfersAsIncExpOption(): Boolean {
        return _filter.selectedTrnTypes.data.contains(TrnType.TRANSFER) &&
                (_trnsStats.transfersInAmount != 0.0 ||
                        _trnsStats.transfersOutAmount != 0.0)
    }

    private suspend fun transfersAsIncomeExpense(transfersAsIncomeExpense: Boolean): suspend () -> ReportState {
        val income = with(_trnsStats) {
            income + if (transfersAsIncomeExpense) transfersInAmount else 0.0
        }

        val expense = with(_trnsStats) {
            expense + if (transfersAsIncomeExpense) transfersOutAmount else 0.0
        }

        val incomeCount = with(_trnsStats) {
            incomesCount + if (transfersAsIncomeExpense) transfersInCount else 0
        }

        val expenseCount = with(_trnsStats) {
            expensesCount + if (transfersAsIncomeExpense) transfersOutCount else 0
        }

        val headerState = stateVal().headerState.copy(
            income = income,
            expenses = expense,
            incomeTransactionsCount = incomeCount,
            expenseTransactionsCount = expenseCount,
            treatTransfersAsIncExp = transfersAsIncomeExpense
        )

        return updateState {
            it.copy(headerState = headerState)
        }.lambda()
    }

    private suspend fun export(context: Context, fileUri: Uri, onFinish: (Uri) -> Unit) {
        val filter = _filter

        if (!filter.validateFilter()) return

        updateState {
            it.copy(loading = true)
        }

        exportCSVLogic.exportToFile(
            context = context,
            fileUri = fileUri,
            exportScope = {
                _allTrns.toOld()
            }
        )

        uiThread {
            onFinish(fileUri)
        }

        updateState {
            it.copy(loading = false)
        }
    }

    private suspend fun filterTransactions(
        baseCurrency: CurrencyCode,
        filter: FilterState
    ) = {
        ByTypeIn(filter.selectedTrnTypes.data.toNonEmptyList()) and
                ByDate(filter.period.data) and
                ByAccount(filter.selectedAcc.data) and
                ByCategoryIn(filter.selectedCat.data.noneCategoryFix().toNonEmptyList())
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

        return brackets(ActualBetween(datePeriod) or DueBetween(datePeriod))
    }

    @Suppress("FunctionName")
    private fun ByAccount(accounts: List<Account>): TrnWhere {
        val nonEmptyList = accounts.toNonEmptyList()
        return brackets(ByAccountIn(nonEmptyList) or ByToAccountIn(nonEmptyList))
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

    private fun clearReportFilter() = suspend {
        val fState = emptyFilterState().copy(
            allAccounts = _allAccounts.toImmutableItem(),
            allCategories = _allCategories.toImmutableItem()
        )
        _filter = fState
        emptyReportScreenState(_baseCurrency)
            .copy(
                filterState = fState,
                trnsList = emptyTransactionList().toImmutableItem()
            )
    }

    private fun FilterState.validateFilter() = when {
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

    private suspend fun handleFilterEvent(filterEvent: ReportFilterEvent): suspend () -> ReportState =
        when (filterEvent) {
            is ReportFilterEvent.SelectTrnsType -> updateTrnsTypeSelection(
                stateVal().filterState,
                filterEvent.type,
                filterEvent.checked
            )

            is ReportFilterEvent.SelectPeriod -> updatePeriodSelection(
                filter = stateVal().filterState,
                timePeriod = filterEvent.timePeriod
            )

            is ReportFilterEvent.SelectAccount -> updateAccountSelection(
                filter = stateVal().filterState,
                account = filterEvent.account,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectCategory -> updateCategorySelection(
                filter = stateVal().filterState,
                category = filterEvent.category,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectAmount -> updateAmountSelection(
                filter = stateVal().filterState,
                amt = filterEvent.amt,
                amountType = filterEvent.amountType
            )

            is ReportFilterEvent.SelectKeyword -> updateIncludeExcludeKeywords(
                filter = stateVal().filterState,
                keyword = filterEvent.keyword,
                keywordsType = filterEvent.keywordsType,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectPlannedPayment -> updatePlannedPaymentSelection(
                filter = stateVal().filterState,
                type = filterEvent.type,
                add = filterEvent.add
            )

            is ReportFilterEvent.Clear -> onFilterClear(
                filter = stateVal().filterState,
                event = filterEvent
            )

            is ReportFilterEvent.SelectAll -> onSelectAll(
                filter = stateVal().filterState,
                event = filterEvent
            )

            is ReportFilterEvent.FilterSet -> setFilter(filter = filterEvent.filter)
        }

    private suspend fun updateTrnsTypeSelection(
        filter: FilterState,
        type: TrnType,
        add: Boolean
    ): suspend () -> ReportState {
        val selectedTrnsList = filter.selectedTrnTypes.data.addOrRemove(add = add, type)

        return updateFilterAndState(filter.copy(selectedTrnTypes = selectedTrnsList.toImmutableItem())).lambda()
    }

    private suspend fun updatePeriodSelection(
        filter: FilterState,
        timePeriod: TimePeriod
    ): suspend () -> ReportState {
        return updateFilterAndState(filter.copy(period = timePeriod.toImmutableItem())).lambda()
    }

    private suspend fun updateAccountSelection(
        filter: FilterState,
        account: Account,
        add: Boolean
    ): suspend () -> ReportState {
        val selectedAccounts =
            filter.selectedAcc.data.addOrRemove(add = add, item = account).toImmutableItem()
        return updateFilterAndState(filter.copy(selectedAcc = selectedAccounts)).lambda()
    }

    private suspend fun updateCategorySelection(
        filter: FilterState,
        category: Category,
        add: Boolean
    ): suspend () -> ReportState {
        val selectedCategories =
            filter.selectedCat.data.addOrRemove(add = add, item = category).toImmutableItem()
        return updateFilterAndState(filter.copy(selectedCat = selectedCategories)).lambda()
    }

    private suspend fun updateAmountSelection(
        filter: FilterState,
        amt: Double?,
        amountType: AmountType
    ): suspend () -> ReportState {
        val updatedFilter = when (amountType) {
            AmountType.MIN -> filter.copy(minAmount = amt)
            AmountType.MAX -> filter.copy(maxAmount = amt)
        }

        return updateFilterAndState(updatedFilter).lambda()
    }

    private suspend fun updateIncludeExcludeKeywords(
        filter: FilterState,
        keyword: String,
        keywordsType: KeywordsType,
        add: Boolean
    ): suspend () -> ReportState {
        val trimmedKeyword = keyword.trim()

        val existingKeywords = when (keywordsType) {
            KeywordsType.INCLUDE -> filter.includeKeywords.data
            KeywordsType.EXCLUDE -> filter.includeKeywords.data
        }

        val updatedKeywords = if (add && trimmedKeyword !in existingKeywords)
            existingKeywords.addOrRemove(add = true, trimmedKeyword)
        else if (!add)
            existingKeywords.addOrRemove(add = false, trimmedKeyword)
        else
            existingKeywords


        val updatedFilter = when (keywordsType) {
            KeywordsType.INCLUDE -> filter.copy(includeKeywords = updatedKeywords.toImmutableItem())
            KeywordsType.EXCLUDE -> filter.copy(excludeKeywords = updatedKeywords.toImmutableItem())
        }

        return updateFilterAndState(updatedFilter).lambda()
    }

    private suspend fun updatePlannedPaymentSelection(
        filter: FilterState,
        type: PlannedPaymentTypes,
        add: Boolean
    ): suspend () -> ReportState {
        val selectPlannedPayments =
            filter.selectedPlannedPayments.data.addOrRemove(add = add, item = type)
                .toImmutableItem()
        return updateFilterAndState(filter.copy(selectedPlannedPayments = selectPlannedPayments)).lambda()
    }

    private suspend fun onFilterClear(
        filter: FilterState,
        event: ReportFilterEvent.Clear
    ): suspend () -> ReportState {
        val updatedFilter = when (event) {
            is ReportFilterEvent.Clear.Filter -> emptyFilterState().copy(
                visible = true,
                allAccounts = _allAccounts.toImmutableItem(),
                allCategories = _allCategories.toImmutableItem()
            )
            ReportFilterEvent.Clear.Accounts -> filter.copy(selectedAcc = emptyList<Account>().toImmutableItem())
            ReportFilterEvent.Clear.Categories -> filter.copy(selectedCat = emptyList<Category>().toImmutableItem())
        }

        return updateFilterAndState(filter = updatedFilter).lambda()
    }

    private suspend fun onSelectAll(
        filter: FilterState,
        event: ReportFilterEvent.SelectAll
    ): suspend () -> ReportState {
        val updatedFilter = when (event) {
            is ReportFilterEvent.SelectAll.Accounts -> filter.copy(
                selectedAcc = _allAccounts.toList().toImmutableItem()
            )
            ReportFilterEvent.SelectAll.Categories -> filter.copy(
                selectedCat = _allCategories.toList().toImmutableItem()
            )
        }

        return updateFilterAndState(filter = updatedFilter).lambda()
    }

    private suspend fun updateFilterAndState(filter: FilterState): ReportState {
        return updateState {
            it.copy(filterState = filter)
        }
    }

    //-------------------------------------- Utility Functions -----------------------------------------

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

    private fun TimePeriod.toPeriodDate(startDateOfMonth: Int): Period {
        val range = toRange(startDateOfMonth)
        val from = range.from().toLocalDate().atStartOfDay()
        val to = range.to().toLocalDate().atEndOfDay()

        return Period.FromTo(from = from, to = to)
    }

    private fun List<Category>.noneCategoryFix() = this.replace(_categoryNone, null)

    private fun <T> List<T>.addOrRemove(add: Boolean, item: T): List<T> {
        return if (add)
            this + item
        else
            this - item
    }
}