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
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.functions.transaction.TrnWhere
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.core.functions.transaction.and
import com.ivy.core.functions.transaction.brackets
import com.ivy.core.functions.transaction.or
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
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.utils.replace
import com.ivy.wallet.utils.scopedIOThread
import com.ivy.wallet.utils.timeNowUTC
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val queryTransactionsAct: TrnsAct,
    private val calculateAct: CalculateWithTransfersAct,
    private val exchangeAct: ExchangeAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val groupTransactionsAct: GroupTrnsAct,
    private val exportCSVLogic: ExportCSVLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
) : FRPViewModel<ReportScreenState, ReportScreenEvent>() {
    override val _state: MutableStateFlow<ReportScreenState> = MutableStateFlow(
        ReportScreenState()
    )

    private val _baseCurrency = MutableStateFlow("")
    private val _filter = MutableStateFlow<ReportFilter?>(null)
    private val allAcc = MutableStateFlow<List<Account>>(emptyList())
    private val allCategories = MutableStateFlow<List<Category>>(emptyList())
    private val transStatsGlobal = MutableStateFlow(ExtendedStats.empty())
    private val allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val categoryNone = dummyCategory(
        name = "None",
        color = Gray.toArgb(),
        icon = dummyIconSized(R.drawable.ic_custom_category_s)
    )

    override suspend fun handleEvent(event: ReportScreenEvent): suspend () -> ReportScreenState =
        withContext(Dispatchers.Default) {
            when (event) {
                is ReportScreenEvent.Start -> initialiseData()
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisibleNew(event.filterOverlayVisible)
                is ReportScreenEvent.OnFilter -> onFilterNew(event.filter)
                is ReportScreenEvent.OnTransfersAsIncomeExpense -> transfersAsIncomeExpense(event.transfersAsIncomeExpense)
                is ReportScreenEvent.OnExportNew -> {
                    exportNew(event.context, event.fileUri, event.onFinish)
                    stateVal().lambda()
                }
            }
        }

    private suspend fun initialiseData() = suspend {
        _baseCurrency.value = baseCurrencyAct(Unit)
        allAcc.value = accountsAct(Unit)
        allCategories.value = listOf(categoryNone) + categoriesAct(Unit)
    } then {
        updateState {
            it.copy(
                baseCurrency = _baseCurrency.value,
                accounts = allAcc.value,
                categories = allCategories.value
            )
        }
    }

    private suspend fun setFilterOverlayVisibleNew(visible: Boolean) = updateState {
        it.copy(filterOverlayVisible = visible)
    }.lambda()

    private suspend fun onFilterNew(filter: ReportFilter?): suspend () -> ReportScreenState {
        suspend fun List<Transaction>.plannedPayments(
            baseCurrCode: CurrencyCode = _baseCurrency.value,
            filterAccounts: List<Account>,
            predicate: (LocalDateTime) -> Boolean
        ): PlannedPaymentsStats {
            val trans = this.filter {
                val date = (it.time as? TrnTime.Due)?.due

                date?.let(predicate) ?: false
            }

            val stats = calculateAct(
                CalculateWithTransfersAct.Input(
                    trns = trans,
                    outputCurrency = baseCurrCode,
                    filterAccounts
                )
            )

            return PlannedPaymentsStats(
                income = stats.income,
                expenses = stats.expense,
                transactions = stats.trns
            )
        }


        return scopedIOThread { scope ->
            //clear filter
            filter ?: return@scopedIOThread clearReportFilter()

            //Report filter Validation
            if (!filter.validateFilter()) return@scopedIOThread stateVal().lambda()

            updateState {
                it.copy(loading = true, filter = filter)
            }

            val timeNowUTC = timeNowUTC()
            val (transactionStats, transactions) =
                filterTransactionsNew(baseCurrency = _baseCurrency.value, filter = filter)

            //Update Global Data
            transStatsGlobal.value = transactionStats
            allTransactions.value = transactions
            _filter.value = filter

            val transactionsWithDateDividers = scope.async {
                groupTransactionsAct(transactions)
            }

            val upcomingPayments = scope.async {
                transactions.plannedPayments(filterAccounts = filter.accounts) {
                    it.isAfter(
                        timeNowUTC
                    )
                }
            }

            val overduePayments = scope.async {
                transactions.plannedPayments(filterAccounts = filter.accounts) {
                    it.isBefore(
                        timeNowUTC
                    )
                }
            }

            updateState {
                it.copy(
                    balance = transactionStats.balance,
                    income = transactionStats.income,
                    expenses = transactionStats.expense,

                    incomeTransactionsCount = transactionStats.incomesCount +
                            if (it.treatTransfersAsIncExp) transactionStats.transfersInCount else 0,

                    expenseTransactionsCount = transactionStats.expensesCount +
                            if (it.treatTransfersAsIncExp) transactionStats.transfersOutCount else 0,

                    filter = filter,
                    baseCurrency = _baseCurrency.value,
                    accounts = allAcc.value,
                    categories = allCategories.value,

                    transactionsWithDateDividers = transactionsWithDateDividers.await(),
                    upcomingPayments = upcomingPayments.await(),
                    overduePayments = overduePayments.await(),

                    loading = false,
                    filterOverlayVisible = false,
                    showTransfersAsIncExpCheckbox = showTransfersAsIncExpOption(),

                    transactionsOld = allTransactions.value.toOld(),
                    accountIdFilters = filter.accounts.map { a -> a.id }
                )
            }.lambda()
        }
    }

    private fun showTransfersAsIncExpOption(): Boolean {
        if (_filter.value == null)
            return false

        /** Show Transfers As Income Expense value if and only if
         *  a) User has selected Transaction.Type == TransactionType.Transfer and
         *  b) There are actual transfer transactions in the queried range
         */
        return _filter.value!!.trnTypes.contains(TrnType.TRANSFER) &&
                (transStatsGlobal.value.transfersInAmount != 0.0 ||
                        transStatsGlobal.value.transfersOutAmount != 0.0)
    }

    private suspend fun transfersAsIncomeExpense(transfersAsIncomeExpense: Boolean): suspend () -> ReportScreenState {
        val income = with(transStatsGlobal.value) {
            income + if (transfersAsIncomeExpense) transfersInAmount else 0.0
        }

        val expense = with(transStatsGlobal.value) {
            expense + if (transfersAsIncomeExpense) transfersOutAmount else 0.0
        }

        val incomeCount = with(transStatsGlobal.value) {
            incomesCount + if (transfersAsIncomeExpense) transfersInCount else 0
        }

        val expenseCount = with(transStatsGlobal.value) {
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

    private suspend fun exportNew(context: Context, fileUri: Uri, onShareUI: (Uri) -> Unit) {
        val filter = stateVal().filter

        filter ?: return
        if (!filter.validateFilter()) return

        updateState {
            it.copy(loading = true)
        }

        exportCSVLogic.exportToFile(
            context = context,
            fileUri = fileUri,
            exportScope = {
                allTransactions.value.toOld()
            }
        )

        uiThread {
            onShareUI(fileUri)
        }

        updateState {
            it.copy(loading = false)
        }
    }

    private suspend fun filterTransactionsNew(
        baseCurrency: String,
        filter: ReportFilter
    ) = {
        ByTypeIn(filter.trnTypes.toNonEmptyList()) and
                ByAllDate(filter.period) and
                filterByAccountIn(filter.accounts) and
                ByCategoryIn(filter.categories.noneCategoryFix().toNonEmptyList())
    } then queryTransactionsAct then {
        filterByAmount(baseCurrency = baseCurrency, filter = filter, transList = it)
    } then {
        filterByWords(filter, it)
    } then { allTrans ->
        val nonPlannedPaymentsTransactions = allTrans.filter { it.time is TrnTime.Actual }
        val input = CalculateWithTransfersAct.Input(
            trns = nonPlannedPaymentsTransactions,
            outputCurrency = baseCurrency,
            selectedAccounts = filter.accounts
        )
        Pair(input, allTrans)
    } thenInvokeAfter {
        val (input, allTrans) = it
        val stats = calculateAct(input)

        Pair(stats, allTrans)
    }

    @Suppress("FunctionName")
    private fun ByAllDate(timePeriod: TimePeriod?): TrnWhere {
        val datePeriod = timePeriod!!.toPeriodDate(1)

        return brackets(ActualBetween(datePeriod) or DueBetween(datePeriod))
    }

    private fun filterByAccountIn(accounts: List<Account>): TrnWhere {
        val nonEmptyList = accounts.toNonEmptyList()
        return brackets(ByAccountIn(nonEmptyList) or ByToAccountIn(nonEmptyList))
    }

    private suspend fun filterByAmount(
        baseCurrency: String,
        filter: ReportFilter,
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
            transFilter: (tAmount: Double) -> Boolean
        ): List<Transaction> {
            return transactionList.filter {
                val tAmount =
                    amountInBaseCurrency(amount = it.value.amount, toCurr = it.account.currency)

                val transactionTransferValue = (it.type as? TransactionType.Transfer)?.toValue

                val toAmountInBaseCurrency = amountInBaseCurrency(
                    transactionTransferValue?.amount,
                    toCurr = transactionTransferValue?.currency
                )

                transFilter(tAmount) || transFilter(toAmountInBaseCurrency)
            }
        }

        return when {
            filter.minAmount != null && filter.maxAmount != null ->
                filterTrans { amt -> amt >= filter.minAmount && amt <= filter.maxAmount }
            filter.minAmount != null -> filterTrans { amt -> amt >= filter.minAmount }
            filter.maxAmount != null -> filterTrans { amt -> amt <= filter.maxAmount }
            else -> {
                transList
            }
        }
    }

    private fun filterByWords(
        filter: ReportFilter,
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

                false
            }
        }

        return transactionsList
            .filterTrans(filter.includeKeywords)
            .filterTrans(filter.excludeKeywords, include = false)
    }

    private suspend fun clearReportFilter() = suspend {
        ReportScreenState(
            baseCurrency = _baseCurrency.value,
            accounts = allAcc.value,
            categories = allCategories.value
        )
    }

    private fun ReportFilter.validateFilter(): Boolean {
        if (trnTypes.isEmpty()) return false

        if (period == null) return false

        if (accounts.isEmpty()) return false

        if (categories.isEmpty()) return false

        if (minAmount != null && maxAmount != null) {
            if (minAmount > maxAmount) return false
            if (maxAmount < minAmount) return false
        }

        return true
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

    private fun List<Category>.noneCategoryFix() = this.replace(categoryNone, null)
}

//    fun start() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _baseCurrency.value = baseCurrencyAct(Unit)
//            _allAccounts.value = accountsAct(Unit)
//            _categories.value = listOf(unSpecifiedCategory) + categoriesAct(Unit)
//
//            updateState {
//                it.copy(
//                    baseCurrency = _baseCurrency.value,
//                    categories = _categories.value,
//                    accounts = _allAccounts.value
//                )
//            }
//        }
//    }

//    private suspend fun setFilter(filter: ReportFilter?) {
//        scopedIOThread { scope ->
//            if (filter == null) {
//                //clear filter
//                _filter.value = null
//                return@scopedIOThread
//            }
//
//            if (!filter.validate()) return@scopedIOThread
//            val accounts = filter.accounts
//            val baseCurrency = baseCurrency.value
//            _filter.value = filter
//
//            updateState {
//                it.copy(loading = true, filter = _filter.value)
//            }
//
//            val transactions = filterTransactions(
//                baseCurrency = baseCurrency,
//                accounts = accounts,
//                filter = filter
//            )
//
//            val history = transactions
//                .filter { it.dateTime != null }
//                .sortedByDescending { it.dateTime }
//
//            val historyWithDateDividers = scope.async {
//                trnsWithDateDivsAct(
//                    TrnsWithDateDivsAct.Input(
//                        baseCurrency = stateVal().baseCurrency,
//                        transactions = history
//                    )
//                )
//            }
//
//            _historyIncomeExpense.value = calcTrnsIncomeExpenseAct(
//                CalcTrnsIncomeExpenseAct.Input(
//                    transactions = history,
//                    accounts = accounts,
//                    baseCurrency = baseCurrency
//                )
//            )
//
//            val income = historyIncomeExpense.value.income.toDouble() +
//                    if (stateVal().treatTransfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0
//
//            val expenses = historyIncomeExpense.value.expense.toDouble() +
//                    if (stateVal().treatTransfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0
//
//            val balance = calculateBalance(historyIncomeExpense.value).toDouble()
//
//            val accountFilterIdList = scope.async { filter.accounts.map { it.id } }
//
//            val timeNowUTC = timeNowUTC()
//
//            //Upcoming
//            val upcomingTransactions = transactions
//                .filter {
//                    it.dueDate != null && it.dueDate!!.isAfter(timeNowUTC)
//                }
//                .sortedBy { it.dueDate }
//
//            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
//                CalcTrnsIncomeExpenseAct.Input(
//                    transactions = upcomingTransactions,
//                    accounts = accounts,
//                    baseCurrency = baseCurrency
//                )
//            )
//            //Overdue
//            val overdue = transactions.filter {
//                it.dueDate != null && it.dueDate!!.isBefore(timeNowUTC)
//            }.sortedByDescending {
//                it.dueDate
//            }
//            val overdueIncomeExpense = calcTrnsIncomeExpenseAct(
//                CalcTrnsIncomeExpenseAct.Input(
//                    transactions = overdue,
//                    accounts = accounts,
//                    baseCurrency = baseCurrency
//                )
//            )
//
//            updateState {
//                it.copy(
//                    income = income,
//                    expenses = expenses,
//                    upcomingIncome = upcomingIncomeExpense.income.toDouble(),
//                    upcomingExpenses = upcomingIncomeExpense.expense.toDouble(),
//                    overdueIncome = overdueIncomeExpense.income.toDouble(),
//                    overdueExpenses = overdueIncomeExpense.expense.toDouble(),
//                    history = historyWithDateDividers.await(),
//                    upcomingTransactions = upcomingTransactions,
//                    overdueTransactions = overdue,
//                    categories = categories.value,
//                    accounts = _allAccounts.value,
//                    filter = filter,
//                    loading = false,
//                    accountIdFilters = accountFilterIdList.await(),
//                    transactions = transactions,
//                    balance = balance,
//                    filterOverlayVisible = false,
//                    showTransfersAsIncExpCheckbox = filter.trnTypes.contains(TrnType.TRANSFER)
//                )
//            }
//        }
//    }

//    private suspend fun filterTransactions(
//        baseCurrency: String,
//        accounts: List<AccountOld>,
//        filter: ReportFilter,
//    ): List<TransactionOld> {
//        val filterAccountIds = filter.accounts.map { it.id }
//        val filterCategoryIds =
//            filter.categories.map { if (it.id == unSpecifiedCategory.id) null else it.id }
//        val filterRange = filter.period?.toRange(ivyContext.startDayOfMonth)
//
//        return transactionDao
//            .findAll()
//            .map { it.toDomain() }
//            .filter {
//                //Filter by Transaction Type
//                filter.trnTypes.contains(it.type)
//            }
//            .filter {
//                //Filter by Time Period
//
//                filterRange ?: return@filter false
//
//                (it.dateTime != null && filterRange.includes(it.dateTime!!)) ||
//                        (it.dueDate != null && filterRange.includes(it.dueDate!!))
//            }
//            .filter { trn ->
//                //Filter by Accounts
//
//                filterAccountIds.contains(trn.accountId) || //Transfers Out
//                        (trn.toAccountId != null && filterAccountIds.contains(trn.toAccountId)) //Transfers In
//            }
//            .filter { trn ->
//                //Filter by Categories
//
//                filterCategoryIds.contains(trn.categoryId) || (trn.type == TrnType.TRANSFER)
//            }
//            .filterSuspend {
//                //Filter by Amount
//                //!NOTE: Amount must be converted to baseCurrency amount
//
//                val trnAmountBaseCurrency = exchangeAct(
//                    ExchangeActOld.Input(
//                        data = ExchangeData(
//                            baseCurrency = baseCurrency,
//                            fromCurrency = trnCurrency(it, accounts, baseCurrency),
//                        ),
//                        amount = it.amount
//                    )
//                ).orZero().toDouble()
//
//                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
//                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
//            }
//            .filter {
//                //Filter by Included Keywords
//
//                val includeKeywords = filter.includeKeywords
//                if (includeKeywords.isEmpty()) return@filter true
//
//                if (it.title != null && it.title!!.isNotEmpty()) {
//                    includeKeywords.forEach { keyword ->
//                        if (it.title!!.containsLowercase(keyword)) {
//                            return@filter true
//                        }
//                    }
//                }
//
//                if (it.description != null && it.description!!.isNotEmpty()) {
//                    includeKeywords.forEach { keyword ->
//                        if (it.description!!.containsLowercase(keyword)) {
//                            return@filter true
//                        }
//                    }
//                }
//
//                false
//            }
//            .filter {
//                //Filter by Excluded Keywords
//
//                val excludedKeywords = filter.excludeKeywords
//                if (excludedKeywords.isEmpty()) return@filter true
//
//                if (it.title != null && it.title!!.isNotEmpty()) {
//                    excludedKeywords.forEach { keyword ->
//                        if (it.title!!.containsLowercase(keyword)) {
//                            return@filter false
//                        }
//                    }
//                }
//
//                if (it.description != null && it.description!!.isNotEmpty()) {
//                    excludedKeywords.forEach { keyword ->
//                        if (it.description!!.containsLowercase(keyword)) {
//                            return@filter false
//                        }
//                    }
//                }
//
//                true
//            }
//            .toList()
//    }

//    private fun String.containsLowercase(anotherString: String): Boolean {
//        return this.toLowerCaseLocal().contains(anotherString.toLowerCaseLocal())
//    }
//
//    private fun calculateBalance(incomeExpenseTransferPair: IncomeExpenseTransferPair): BigDecimal {
//        return incomeExpenseTransferPair.income + incomeExpenseTransferPair.transferIncome - incomeExpenseTransferPair.expense - incomeExpenseTransferPair.transferExpense
//    }

//    private suspend fun export(context: Context) {
//        val filter = _filter.value ?: return
//        if (!filter.validate()) return
//        val accounts = _allAccounts.value
//        val baseCurrency = _baseCurrency.value
//
//        ivyContext.createNewFile(
//            "Report (${
//                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
//            }).csv"
//        ) { fileUri ->
//            viewModelScope.launch {
//                updateState {
//                    it.copy(loading = true)
//                }
//
//                exportCSVLogic.exportToFile(
//                    context = context,
//                    fileUri = fileUri,
//                    exportScope = {
//                        filterTransactions(
//                            baseCurrency = baseCurrency,
//                            accounts = accounts,
//                            filter = filter
//                        )
//                    }
//                )
//
//                (context as com.ivy.core.ui.temp.RootScreen).shareCSVFile(
//                    fileUri = fileUri
//                )
//
//                updateState {
//                    it.copy(loading = false)
//                }
//            }
//        }
//    }

//    private fun setUpcomingExpanded(expanded: Boolean) {
//        updateStateNonBlocking {
//            it.copy(upcomingExpanded = expanded)
//        }
//    }
//
//    private fun setOverdueExpanded(expanded: Boolean) {
//        updateStateNonBlocking {
//            it.copy(overdueExpanded = expanded)
//        }
//    }
//
//    private suspend fun payOrGet(transaction: TransactionOld) {
//
//    }
//
//    private fun setFilterOverlayVisible(filterOverlayVisible: Boolean) {
//        updateStateNonBlocking {
//            it.copy(filterOverlayVisible = filterOverlayVisible)
//        }
//    }

//    private suspend fun onTreatTransfersAsIncomeExpense(treatTransfersAsIncExp: Boolean) {
//        updateState {
//            val income = historyIncomeExpense.value.income.toDouble() +
//                    if (treatTransfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0
//            val expenses = historyIncomeExpense.value.expense.toDouble() +
//                    if (treatTransfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0
//            it.copy(
//                treatTransfersAsIncExp = treatTransfersAsIncExp,
//                income = income,
//                expenses = expenses
//            )
//        }
//    }
//
//    fun onEventHand(event: ReportScreenEvent) {
//        viewModelScope.launch(Dispatchers.Default) {
//            when (event) {
//                is ReportScreenEvent.OnFilter -> {
//                    try{
//                        //setFilter(event.filter)
//                        onFilterNew(event.filter)
//                    }catch (e:Exception){
//                        Log.d("GGGG","Error"+e.message)
//                        Log.d("GGGG","Error "+ Log.getStackTraceString(e))
//                    }
//                }
//                is ReportScreenEvent.OnExport -> export(event.context)
//                is ReportScreenEvent.OnOverdueExpanded -> setOverdueExpanded(event.overdueExpanded)
//                is ReportScreenEvent.OnUpcomingExpanded -> setUpcomingExpanded(event.upcomingExpanded)
//                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
//                is ReportScreenEvent.OnTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
//                    event.transfersAsIncomeExpense
//                )
//                else -> {}
//            }
//        }
//    }