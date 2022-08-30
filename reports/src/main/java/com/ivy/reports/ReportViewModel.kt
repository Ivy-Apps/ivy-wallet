package com.ivy.reports

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.ivy.base.R
import com.ivy.common.atEndOfDay
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
import com.ivy.reports.ui.*
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
) : FRPViewModel<ReportScreenState, ReportScreenEvent>() {
    override val _state: MutableStateFlow<ReportScreenState> = MutableStateFlow(
        emptyReportScreenState(baseCurrency = getDefaultCurrencyCode())
    )

    // "_" (Underscore Prefix) for all global variables
    private var _baseCurrency: CurrencyCode = getDefaultCurrencyCode()
    private var _filter: ReportFilter = emptyReportFilter(_baseCurrency)

    private var _allAccounts: List<Account> = emptyList()
    private var _allCategories: List<Category> = emptyList()

    private var _trnsStats: ExtendedStats = ExtendedStats.empty()
    private var _allTrns: List<Transaction> = emptyList()

    private val _categoryNone = dummyCategory(
        name = "None",
        color = Gray.toArgb(),
        icon = dummyIconSized(R.drawable.ic_custom_category_s)
    )

    override suspend fun handleEvent(event: ReportScreenEvent): suspend () -> ReportScreenState =
        withContext(Dispatchers.Default) {
            when (event) {
                is ReportScreenEvent.Start -> initialiseData()
                is ReportScreenEvent.FilterOptions -> setFilterOptionsVisibility(event.visible)
                is ReportScreenEvent.TransfersAsIncomeExpense -> transfersAsIncomeExpense(event.transfersAsIncomeExpense)
                is ReportScreenEvent.Export -> {
                    export(event.context, event.fileUri, event.onFinish)
                    stateVal().lambda()
                }
                is ReportScreenEvent.FilterEvent -> {
                    handleFilterEvent(event.filterEvent)
                }
            }
        }

    private fun initialiseData() = suspend {
        _baseCurrency = baseCurrencyAct(Unit)
        _allAccounts = accountsAct(Unit)
        _allCategories = listOf(_categoryNone) + categoriesAct(Unit)

        //Copy existing filter object with the new baseCurrency to preserve filterOptions state,
        _filter = _filter.copy(currency = _baseCurrency)

        updateState {
            it.copy(
                baseCurrency = _baseCurrency,
                accounts = _allAccounts,
                categories = _allCategories,
                filter = _filter
            )
        }
    }

    private suspend fun setFilterOptionsVisibility(visible: Boolean) = updateState {
        it.copy(filterOptionsVisibility = visible)
    }.lambda()

    private suspend fun setFilter(filter: ReportFilter): suspend () -> ReportScreenState {
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

            updateGlobalData(transactionStats, transactions, filter)

            val trnsList = groupTrnsAct(transactions)

            updateState {
                it.copy(
                    balance = transactionStats.balance,
                    income = transactionStats.income,
                    expenses = transactionStats.expense,

                    incomeTransactionsCount = transactionStats.incomesCount,
                    expenseTransactionsCount = transactionStats.expensesCount,

                    filter = filter,
                    baseCurrency = _baseCurrency,
                    accounts = _allAccounts,
                    categories = _allCategories,

                    trnsList = trnsList,

                    loading = false,
                    filterOptionsVisibility = false,
                    treatTransfersAsIncExp = false,
                    showTransfersAsIncExpCheckbox = showTransfersAsIncExpOption(),

                    transactionsOld = _allTrns.toOld(),
                    accountIdFilters = filter.accounts.map { a -> a.id }
                )
            }.lambda()
        }
    }

    private fun ReportFilter.hasEmptyContents() =
        this.trnTypes.isEmpty() && period == null &&
                accounts.isEmpty() && categories.isEmpty() &&
                minAmount == null && maxAmount == null &&
                includeKeywords.isEmpty() && excludeKeywords.isEmpty()

    private fun updateGlobalData(
        transactionStats: ExtendedStats,
        transactions: List<Transaction>,
        filter: ReportFilter
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
        return _filter.trnTypes.contains(TrnType.TRANSFER) &&
                (_trnsStats.transfersInAmount != 0.0 ||
                        _trnsStats.transfersOutAmount != 0.0)
    }

    private suspend fun transfersAsIncomeExpense(transfersAsIncomeExpense: Boolean): suspend () -> ReportScreenState {
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

        return updateState {
            it.copy(
                income = income,
                expenses = expense,
                incomeTransactionsCount = incomeCount,
                expenseTransactionsCount = expenseCount,
                treatTransfersAsIncExp = transfersAsIncomeExpense
            )
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
        filter: ReportFilter
    ) = {
        ByTypeIn(filter.trnTypes.toNonEmptyList()) and
                ByDate(filter.period) and
                ByAccount(filter.accounts) and
                ByCategoryIn(filter.categories.noneCategoryFix().toNonEmptyList())
    } then queryTrnsAct then {
        filterByAmount(
            baseCurrency = baseCurrency,
            minAmt = filter.minAmount,
            maxAmt = filter.maxAmount,
            transList = it
        )
    } then {
        filterByWords(
            includeKeywords = filter.includeKeywords,
            excludeKeywords = filter.excludeKeywords,
            transactionsList = it
        )
    } thenInvokeAfter { allTrans ->
        /**
         * [actualTrns] variable represents transactions without PlannedPayment Transactions
         */
        val actualTrns = allTrans.filter(::actual)

        val stats = calculateAct(
            CalculateWithTransfersAct.Input(
                trns = actualTrns,
                outputCurrency = baseCurrency,
                accounts = filter.accounts
            )
        )

        Pair(stats, allTrans)
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
        emptyReportScreenState(_baseCurrency)
            .copy(
                accounts = _allAccounts,
                categories = _allCategories,
                trnsList = emptyTransactionList()
            )
    }

    private fun ReportFilter.validateFilter() = when {
        trnTypes.isEmpty() -> false

        period == null -> false

        accounts.isEmpty() -> false

        categories.isEmpty() -> false

        minAmount != null && maxAmount != null -> when {
            minAmount > maxAmount -> false
            maxAmount < minAmount -> false
            else -> true
        }

        else -> true
    }

    private suspend fun handleFilterEvent(filterEvent: ReportFilterEvent): suspend () -> ReportScreenState =
        when (filterEvent) {
            is ReportFilterEvent.SelectTrnsType -> updateTrnsTypeSelection(
                stateVal().filter,
                filterEvent.type,
                filterEvent.checked
            )

            is ReportFilterEvent.SelectPeriod -> updatePeriodSelection(
                filter = stateVal().filter,
                timePeriod = filterEvent.timePeriod
            )

            is ReportFilterEvent.SelectAccount -> updateAccountSelection(
                filter = stateVal().filter,
                account = filterEvent.account,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectCategory -> updateCategorySelection(
                filter = stateVal().filter,
                category = filterEvent.category,
                add = filterEvent.add
            )

            is ReportFilterEvent.SelectAmount -> updateAmountSelection(
                filter = stateVal().filter,
                amt = filterEvent.amt,
                amountFilterType = filterEvent.amountFilterType
            )

            is ReportFilterEvent.SelectKeyword -> updateIncludeExcludeKeywords(
                filter = stateVal().filter,
                keyword = filterEvent.keyword,
                keywordsFilterType = filterEvent.keywordsFilterType,
                add = filterEvent.add
            )

            is ReportFilterEvent.Clear -> onFilterClear(
                filter = stateVal().filter,
                type = filterEvent.type
            )

            is ReportFilterEvent.SelectAll -> onSelectAll(
                filter = stateVal().filter,
                type = filterEvent.type
            )

            is ReportFilterEvent.FilterSet -> setFilter(filter = filterEvent.filter)
        }

    private suspend fun updateTrnsTypeSelection(
        filter: ReportFilter,
        type: TrnType,
        add: Boolean
    ): suspend () -> ReportScreenState {
        val selectedTrnsList = filter.trnTypes.addOrRemove(add = add, type)

        return updateFilterAndState(filter.copy(trnTypes = selectedTrnsList)).lambda()
    }

    private suspend fun updatePeriodSelection(
        filter: ReportFilter,
        timePeriod: TimePeriod
    ): suspend () -> ReportScreenState {
        return updateFilterAndState(filter.copy(period = timePeriod)).lambda()
    }

    private suspend fun updateAccountSelection(
        filter: ReportFilter,
        account: Account,
        add: Boolean
    ): suspend () -> ReportScreenState {
        val selectedAccounts = filter.accounts.addOrRemove(add = add, item = account)
        return updateFilterAndState(filter.copy(accounts = selectedAccounts)).lambda()
    }

    private suspend fun updateCategorySelection(
        filter: ReportFilter,
        category: Category,
        add: Boolean
    ): suspend () -> ReportScreenState {
        val selectedCategories = filter.categories.addOrRemove(add = add, item = category)
        return updateFilterAndState(filter.copy(categories = selectedCategories)).lambda()
    }

    private suspend fun updateAmountSelection(
        filter: ReportFilter,
        amt: Double?,
        amountFilterType: AmountFilterType
    ): suspend () -> ReportScreenState {
        val updatedFilter = when (amountFilterType) {
            AmountFilterType.MIN -> filter.copy(minAmount = amt)
            AmountFilterType.MAX -> filter.copy(maxAmount = amt)
        }

        return updateFilterAndState(updatedFilter).lambda()
    }

    private suspend fun updateIncludeExcludeKeywords(
        filter: ReportFilter,
        keyword: String,
        keywordsFilterType: KeywordsFilterType,
        add: Boolean
    ): suspend () -> ReportScreenState {
        val trimmedKeyword = keyword.trim()

        val existingKeywords = when (keywordsFilterType) {
            KeywordsFilterType.INCLUDE -> filter.includeKeywords
            KeywordsFilterType.EXCLUDE -> filter.includeKeywords
        }

        val updatedKeywords = if (add && trimmedKeyword !in existingKeywords)
            existingKeywords.addOrRemove(add = true, trimmedKeyword)
        else if (!add)
            existingKeywords.addOrRemove(add = false, trimmedKeyword)
        else
            existingKeywords


        val updatedFilter = when (keywordsFilterType) {
            KeywordsFilterType.INCLUDE -> filter.copy(includeKeywords = updatedKeywords)
            KeywordsFilterType.EXCLUDE -> filter.copy(excludeKeywords = updatedKeywords)
        }

        return updateFilterAndState(updatedFilter).lambda()
    }

    private suspend fun onFilterClear(
        filter: ReportFilter,
        type: ClearType
    ): suspend () -> ReportScreenState {
        val updatedFilter = when (type) {
            ClearType.ALL -> emptyReportFilter(baseCurrency = _baseCurrency)
            ClearType.ACCOUNTS -> filter.copy(accounts = emptyList())
            ClearType.CATEGORIES -> filter.copy(categories = emptyList())
        }

        return updateFilterAndState(filter = updatedFilter).lambda()
    }

    private suspend fun onSelectAll(
        filter: ReportFilter,
        type: SelectType
    ): suspend () -> ReportScreenState {
        val updatedFilter = when (type) {
            SelectType.ACCOUNTS -> filter.copy(accounts = _allAccounts.toList())
            SelectType.CATEGORIES -> filter.copy(categories = _allCategories.toList())
        }

        return updateFilterAndState(filter = updatedFilter).lambda()
    }

    private suspend fun updateFilterAndState(filter: ReportFilter): ReportScreenState {
        return updateState {
            it.copy(filter = filter)
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