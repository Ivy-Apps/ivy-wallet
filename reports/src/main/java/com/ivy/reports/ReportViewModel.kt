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
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
                is ReportScreenEvent.OnFilter -> onFilter(event.filter)
                is ReportScreenEvent.OnTransfersAsIncomeExpense -> transfersAsIncomeExpense(event.transfersAsIncomeExpense)
                is ReportScreenEvent.OnExport -> {
                    export(event.context, event.fileUri, event.onFinish)
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

    private suspend fun setFilterOverlayVisible(visible: Boolean) = updateState {
        it.copy(filterOverlayVisible = visible)
    }.lambda()

    private suspend fun onFilter(filter: ReportFilter?): suspend () -> ReportScreenState {
        return scopedIOThread { scope ->
            //clear filter
            filter ?: return@scopedIOThread clearReportFilter()

            //Report filter Validation
            if (!filter.validateFilter()) return@scopedIOThread stateVal().lambda()

            updateState {
                it.copy(loading = true, filter = filter)
            }

            val (transactionStats, transactions) =
                filterTransactions(baseCurrency = _baseCurrency.value, filter = filter)

            //Update Global Data
            transStatsGlobal.value = transactionStats
            allTransactions.value = transactions
            _filter.value = filter

            val transactionsWithDateDividers = groupTransactionsAct(transactions)

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

                    transactionsWithDateDividers = transactionsWithDateDividers,

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

    private suspend fun export(context: Context, fileUri: Uri, onShareUI: (Uri) -> Unit) {
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

    private suspend fun filterTransactions(
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

    private fun clearReportFilter() = suspend {
        ReportScreenState(
            baseCurrency = _baseCurrency.value,
            accounts = allAcc.value,
            categories = allCategories.value,
            transactionsWithDateDividers = emptyTransactionList()
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