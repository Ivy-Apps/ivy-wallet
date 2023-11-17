package com.ivy.reports

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.legacy.stringRes
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.domain.ComposeViewModel
import com.ivy.domain.RootScreen
import com.ivy.frp.filterSuspend
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.utils.formatNicelyWithTime
import com.ivy.legacy.utils.scopedIOThread
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.toLowerCaseLocal
import com.ivy.legacy.utils.uiThread
import com.ivy.resources.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import com.ivy.wallet.domain.pure.util.orZero
import com.ivy.wallet.ui.theme.Gray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val transactionDao: TransactionDao,
    private val ivyContext: IvyWalletCtx,
    private val exportCSVLogic: ExportCSVLogic,
    private val exchangeAct: ExchangeAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : ComposeViewModel<ReportScreenState, ReportScreenEvent>() {
    private val unSpecifiedCategory =
        Category(stringRes(R.string.unspecified), color = Gray.toArgb())
    private val baseCurrency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val historyIncomeExpense = mutableStateOf(IncomeExpenseTransferPair.zero())
    private val filter = mutableStateOf<ReportFilter?>(null)
    private val balance = mutableDoubleStateOf(0.0)
    private val income = mutableDoubleStateOf(0.0)
    private val expenses = mutableDoubleStateOf(0.0)
    private val upcomingIncome = mutableDoubleStateOf(0.0)
    private val upcomingExpenses = mutableDoubleStateOf(0.0)
    private val overdueIncome = mutableDoubleStateOf(0.0)
    private val overdueExpenses = mutableDoubleStateOf(0.0)
    private val history = mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val upcomingTransactions = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val overdueTransactions = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val upcomingExpanded = mutableStateOf(false)
    private val overdueExpanded = mutableStateOf(false)
    private val loading = mutableStateOf(false)
    private val accountIdFilters = mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private val transactions = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val filterOverlayVisible = mutableStateOf(false)
    private val showTransfersAsIncExpCheckbox = mutableStateOf(false)
    private val treatTransfersAsIncExp = mutableStateOf(false)

    @Composable
    override fun uiState(): ReportScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return ReportScreenState(
            categories = categories.value,
            accounts = accounts.value,
            accountIdFilters = accountIdFilters.value,
            balance = balance.doubleValue,
            baseCurrency = baseCurrency.value,
            expenses = expenses.doubleValue,
            filter = filter.value,
            filterOverlayVisible = filterOverlayVisible.value,
            history = history.value,
            income = income.doubleValue,
            loading = loading.value,
            overdueExpanded = overdueExpanded.value,
            overdueExpenses = overdueExpenses.doubleValue,
            overdueIncome = overdueIncome.doubleValue,
            overdueTransactions = overdueTransactions.value,
            showTransfersAsIncExpCheckbox = showTransfersAsIncExpCheckbox.value,
            transactions = transactions.value,
            treatTransfersAsIncExp = treatTransfersAsIncExp.value,
            upcomingExpanded = upcomingExpanded.value,
            upcomingExpenses = upcomingExpenses.doubleValue,
            upcomingIncome = upcomingIncome.doubleValue,
            upcomingTransactions = upcomingTransactions.value
        )
    }

    override fun onEvent(event: ReportScreenEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is ReportScreenEvent.OnFilter -> setFilter(event.filter)
                is ReportScreenEvent.OnExport -> export(event.context)
                is ReportScreenEvent.OnPayOrGet -> payOrGet(event.transaction)
                is ReportScreenEvent.SkipTransaction -> skipTransaction(event.transaction)
                is ReportScreenEvent.SkipTransactions -> skipTransactions(event.transactions)
                is ReportScreenEvent.OnOverdueExpanded -> setOverdueExpanded(event.overdueExpanded)
                is ReportScreenEvent.OnUpcomingExpanded -> setUpcomingExpanded(event.upcomingExpanded)
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
                is ReportScreenEvent.OnTreatTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
                    event.transfersAsIncomeExpense
                )
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            baseCurrency.value = baseCurrencyAct(Unit)
            accounts.value = accountsAct(Unit)
            categories.value =
                (listOf(unSpecifiedCategory) + categoriesAct(Unit)).toImmutableList()
        }
    }

    private suspend fun setFilter(reportFilter: ReportFilter?) {
        scopedIOThread { scope ->
            if (reportFilter == null) {
                //clear filter
                filter.value = null
                return@scopedIOThread
            }

            if (!reportFilter.validate()) return@scopedIOThread
            val tempAccounts = reportFilter.accounts
            val baseCurrency = baseCurrency.value
            filter.value = reportFilter
            loading.value = true

            val transactionsList = filterTransactions(
                baseCurrency = baseCurrency,
                accounts = tempAccounts,
                filter = reportFilter
            )

            val tempHistory = transactionsList
                .filter { it.dateTime != null }
                .sortedByDescending { it.dateTime }

            val historyWithDateDividers = scope.async {
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrency,
                        transactions = tempHistory
                    )
                )
            }

            historyIncomeExpense.value = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = tempHistory,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            val tempIncome = historyIncomeExpense.value.income.toDouble() +
                    if (treatTransfersAsIncExp.value) historyIncomeExpense.value.transferIncome.toDouble() else 0.0

            val tempExpenses = historyIncomeExpense.value.expense.toDouble() +
                    if (treatTransfersAsIncExp.value) historyIncomeExpense.value.transferExpense.toDouble() else 0.0

            val tempBalance = calculateBalance(historyIncomeExpense.value).toDouble()

            val accountFilterIdList = scope.async { reportFilter.accounts.map { it.id } }

            val timeNowUTC = timeNowUTC()

            //Upcoming
            val upcomingTransactionsList = transactionsList
                .filter {
                    it.dueDate != null && it.dueDate!!.isAfter(timeNowUTC)
                }
                .sortedBy { it.dueDate }
                .toImmutableList()

            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = upcomingTransactionsList,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )
            //Overdue
            val overdue = transactionsList.filter {
                it.dueDate != null && it.dueDate!!.isBefore(timeNowUTC)
            }.sortedByDescending {
                it.dueDate
            }.toImmutableList()
            val overdueIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = overdue,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            income.doubleValue = tempIncome
            expenses.doubleValue = tempExpenses
            upcomingExpenses.doubleValue = upcomingIncomeExpense.expense.toDouble()
            upcomingIncome.doubleValue = upcomingIncomeExpense.income.toDouble()
            overdueIncome.doubleValue = overdueIncomeExpense.income.toDouble()
            overdueExpenses.doubleValue = overdueIncomeExpense.expense.toDouble()
            history.value = historyWithDateDividers.await().toImmutableList()
            upcomingTransactions.value = upcomingTransactionsList
            overdueTransactions.value = overdue
            accounts.value = tempAccounts.toImmutableList()
            filter.value = reportFilter
            loading.value = false
            accountIdFilters.value = accountFilterIdList.await().toImmutableList()
            transactions.value = transactionsList
            balance.doubleValue = tempBalance
            filterOverlayVisible.value = false
            showTransfersAsIncExpCheckbox.value = reportFilter.trnTypes.contains(TransactionType.TRANSFER)
        }
    }

    private suspend fun filterTransactions(
        baseCurrency: String,
        accounts: List<Account>,
        filter: ReportFilter,
    ): ImmutableList<Transaction> {
        val filterAccountIds = filter.accounts.map { it.id }
        val filterCategoryIds =
            filter.categories.map { if (it.id == unSpecifiedCategory.id) null else it.id }
        val filterRange = filter.period?.toRange(ivyContext.startDayOfMonth)

        return transactionDao
            .findAll()
            .map { it.toDomain() }
            .filter {
                //Filter by Transaction Type
                filter.trnTypes.contains(it.type)
            }
            .filter {
                //Filter by Time Period

                filterRange ?: return@filter false

                (it.dateTime != null && filterRange.includes(it.dateTime!!)) ||
                        (it.dueDate != null && filterRange.includes(it.dueDate!!))
            }
            .filter { trn ->
                //Filter by Accounts

                filterAccountIds.contains(trn.accountId) || //Transfers Out
                        (trn.toAccountId != null && filterAccountIds.contains(trn.toAccountId)) //Transfers In
            }
            .filter { trn ->
                //Filter by Categories

                filterCategoryIds.contains(trn.categoryId) || (trn.type == TransactionType.TRANSFER)
            }
            .filterSuspend {
                //Filter by Amount
                //!NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeAct(
                    ExchangeAct.Input(
                        data = ExchangeData(
                            baseCurrency = baseCurrency,
                            fromCurrency = trnCurrency(it, accounts, baseCurrency),
                        ),
                        amount = it.amount
                    )
                ).orZero().toDouble()

                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
            }
            .filter {
                //Filter by Included Keywords

                val includeKeywords = filter.includeKeywords
                if (includeKeywords.isEmpty()) return@filter true

                if (it.title != null && it.title!!.isNotEmpty()) {
                    includeKeywords.forEach { keyword ->
                        if (it.title!!.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                if (it.description != null && it.description!!.isNotEmpty()) {
                    includeKeywords.forEach { keyword ->
                        if (it.description!!.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                false
            }
            .filter {
                //Filter by Excluded Keywords

                val excludedKeywords = filter.excludeKeywords
                if (excludedKeywords.isEmpty()) return@filter true

                if (it.title != null && it.title!!.isNotEmpty()) {
                    excludedKeywords.forEach { keyword ->
                        if (it.title!!.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }

                if (it.description != null && it.description!!.isNotEmpty()) {
                    excludedKeywords.forEach { keyword ->
                        if (it.description!!.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }

                true
            }
            .toImmutableList()
    }

    private fun String.containsLowercase(anotherString: String): Boolean {
        return this.toLowerCaseLocal().contains(anotherString.toLowerCaseLocal())
    }

    private fun calculateBalance(incomeExpenseTransferPair: IncomeExpenseTransferPair): BigDecimal {
        return incomeExpenseTransferPair.income + incomeExpenseTransferPair.transferIncome - incomeExpenseTransferPair.expense - incomeExpenseTransferPair.transferExpense
    }

    private suspend fun export(context: Context) {
        val filter = filter.value ?: return
        if (!filter.validate()) return

        ivyContext.createNewFile(
            "Report (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).csv"
        ) { fileUri ->
            viewModelScope.launch {
                loading.value = true

                exportCSVLogic.exportToFile(
                    context = context,
                    fileUri = fileUri,
                    exportScope = {
                        filterTransactions(
                            baseCurrency = baseCurrency.value,
                            accounts = accounts.value,
                            filter = filter
                        )
                    }
                )

                (context as RootScreen).shareCSVFile(
                    fileUri = fileUri
                )

                loading.value = false
            }
        }
    }

    private fun setUpcomingExpanded(expanded: Boolean) {
        upcomingExpanded.value = expanded
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        overdueExpanded.value = expanded
    }

    private suspend fun payOrGet(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start()
                setFilter(filter.value)
            }
        }
    }

    private fun setFilterOverlayVisible(visible: Boolean) {
        filterOverlayVisible.value = visible
    }

    private fun onTreatTransfersAsIncomeExpense(transfersAsIncExp: Boolean) {
            income.doubleValue = historyIncomeExpense.value.income.toDouble() +
                    if (transfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0
            expenses.doubleValue = historyIncomeExpense.value.expense.toDouble() +
                    if (transfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0
                treatTransfersAsIncExp.value = transfersAsIncExp
    }

    private suspend fun skipTransaction(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transaction = transaction,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    private suspend fun skipTransactions(transactions: List<Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }
}
