package com.ivy.reports

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.LegacyTransaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.legacy.stringRes
import com.ivy.base.model.TransactionType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Tag
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.TagRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.temp.migration.getTransactionType
import com.ivy.data.temp.migration.getValue
import com.ivy.domain.RootScreen
import com.ivy.domain.usecase.csv.ExportCsvUseCase
import com.ivy.frp.filterSuspend
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.temp.toLegacy
import com.ivy.legacy.utils.getISOFormattedDateTime
import com.ivy.legacy.utils.scopedIOThread
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.toLowerCaseLocal
import com.ivy.legacy.utils.uiThread
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val transactionRepository: TransactionRepository,
    private val ivyContext: IvyWalletCtx,
    private val exchangeAct: ExchangeAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val transactionMapper: TransactionMapper,
    private val tagRepository: TagRepository,
    private val exportCsvUseCase: ExportCsvUseCase,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) : ComposeViewModel<ReportScreenState, ReportScreenEvent>() {
    private val unSpecifiedCategory =
        Category(
            name = NotBlankTrimmedString.unsafe(stringRes(R.string.unspecified)),
            color = ColorInt(Gray.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
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
    private val upcomingTransactions =
        mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val overdueTransactions =
        mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val upcomingExpanded = mutableStateOf(false)
    private val overdueExpanded = mutableStateOf(false)
    private val loading = mutableStateOf(false)
    private val accountIdFilters = mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private val transactions = mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private val filterOverlayVisible = mutableStateOf(false)
    private val showTransfersAsIncExpCheckbox = mutableStateOf(false)
    private val treatTransfersAsIncExp = mutableStateOf(false)
    private val allTags = mutableStateOf<ImmutableList<Tag>>(persistentListOf())

    private var tagSearchJob: Job? = null
    private val tagSearchDebounceTimeInMills: Long = 500

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
            upcomingTransactions = upcomingTransactions.value,
            allTags = allTags.value
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
                is ReportScreenEvent.OnPayOrGetLegacy -> payOrGetLegacy(event.transaction)
                is ReportScreenEvent.SkipTransactionLegacy -> skipTransactionLegacy(event.transaction)
                is ReportScreenEvent.SkipTransactionsLegacy -> skipTransactionsLegacy(event.transactions)
                is ReportScreenEvent.OnOverdueExpanded -> setOverdueExpanded(event.overdueExpanded)
                is ReportScreenEvent.OnUpcomingExpanded -> setUpcomingExpanded(event.upcomingExpanded)
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
                is ReportScreenEvent.OnTreatTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
                    event.transfersAsIncomeExpense
                )

                is ReportScreenEvent.OnTagSearch -> onTagSearch(event.data)
            }
        }
    }

    private suspend fun onTagSearch(query: String) {
        withContext(Dispatchers.IO) {
            tagSearchJob?.cancelAndJoin()
            delay(tagSearchDebounceTimeInMills) // Debounce effect
            tagSearchJob = launch(Dispatchers.IO) {
                NotBlankTrimmedString.from(query.toLowerCaseLocal())
                    .fold(
                        ifRight = {
                            allTags.value =
                                tagRepository.findByText(text = it.value).toImmutableList()
                        },
                        ifLeft = {
                            allTags.value = tagRepository.findAll().toImmutableList()
                        }
                    )
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            baseCurrency.value = baseCurrencyAct(Unit)
            accounts.value = accountsAct(Unit)
            categories.value =
                (listOf(unSpecifiedCategory) + categoryRepository.findAll()).toImmutableList()
            allTags.value = tagRepository.findAll().toImmutableList()
        }
    }

    private suspend fun setFilter(reportFilter: ReportFilter?) {
        scopedIOThread { scope ->
            if (reportFilter == null) {
                // clear filter
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
                .sortedByDescending { it.time }

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

            // Upcoming
            val upcomingTransactionsList = transactionsList
                .filter {
                    !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                        .isAfter(timeNowUTC)
                }
                .sortedBy { it.time }
                .toImmutableList()

            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = upcomingTransactionsList,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )
            // Overdue
            val overdue = transactionsList.filter {
                !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                    .isBefore(timeNowUTC)
            }.sortedByDescending {
                it.time
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
            upcomingTransactions.value = upcomingTransactionsList.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            overdueTransactions.value = overdue.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            accounts.value = tempAccounts.toImmutableList()
            filter.value = reportFilter
            loading.value = false
            accountIdFilters.value = accountFilterIdList.await().toImmutableList()
            transactions.value = transactionsList.map {
                it.toLegacy(transactionMapper)
            }.toImmutableList()
            balance.doubleValue = tempBalance
            filterOverlayVisible.value = false
            showTransfersAsIncExpCheckbox.value =
                reportFilter.trnTypes.contains(TransactionType.TRANSFER)
        }
    }

    private suspend fun filterTransactions(
        baseCurrency: String,
        accounts: List<Account>,
        filter: ReportFilter,
    ): ImmutableList<Transaction> {
        val filterAccountIds = filter.accounts.map { it.id }
        val filterCategoryIds =
            filter.categories.map { if (it.id.value == unSpecifiedCategory.id.value) null else it.id }
        val filterRange =
            filter.period?.toRange(ivyContext.startDayOfMonth, timeConverter, timeProvider)

        val transactions = if (filter.selectedTags.isNotEmpty()) {
            tagRepository.findByAllAssociatedIdForTagId(filter.selectedTags)
                .asSequence()
                .flatMap { it.value }
                .map { TransactionId(it.associatedId.value) }
                .distinct()
                .toList()
                .let {
                    transactionRepository.findByIds(it)
                }
        } else {
            transactionRepository.findAll()
        }

        return transactions
            .filter {
                with(transactionMapper) {
                    filter.trnTypes.contains(it.getTransactionType())
                }
            }
            .filter {
                // Filter by Time Period

                filterRange ?: return@filter false

                filterRange.includes(it.time)
            }
            .filter { trn ->
                // Filter by Accounts
                when (trn) {
                    is Transfer -> {
                        filterAccountIds.contains(trn.fromAccount.value) || // Transfers Out
                                (filterAccountIds.contains(trn.toAccount.value)) // Transfers In
                    }

                    is Expense -> {
                        filterAccountIds.contains(trn.account.value)
                    }

                    is Income -> {
                        filterAccountIds.contains(trn.account.value)
                    }
                }
            }
            .filter { trn ->
                // Filter by Categories

                filterCategoryIds.contains(trn.category) || with(transactionMapper) {
                    (trn.getTransactionType() == TransactionType.TRANSFER)
                }
            }
            .filterSuspend {
                // Filter by Amount
                // !NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeAct(
                    ExchangeAct.Input(
                        data = ExchangeData(
                            baseCurrency = baseCurrency,
                            fromCurrency = trnCurrency(it, accounts, baseCurrency),
                        ),
                        amount = it.getValue()
                    )
                ).orZero().toDouble()

                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
            }
            .filter {
                // Filter by Included Keywords

                val includeKeywords = filter.includeKeywords
                if (includeKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    includeKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                it.description?.let { description ->
                    includeKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                false
            }
            .filter {
                // Filter by Excluded Keywords

                val excludedKeywords = filter.excludeKeywords
                if (excludedKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    excludedKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                it.description?.let { description ->
                    excludedKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                true
            }.toImmutableList()
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
            "IvyWalletReport-${
                timeNowUTC().getISOFormattedDateTime()
            }.csv"
        ) { fileUri ->
            viewModelScope.launch {
                loading.value = true

                exportCsvUseCase.exportToFile(
                    outputFile = fileUri,
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
            plannedPaymentsLogic.payOrGet(
                transaction = transaction
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun payOrGetLegacy(transaction: com.ivy.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(transaction = transaction) {
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

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionLegacy(transaction: com.ivy.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
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

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionsLegacy(transactions: List<com.ivy.base.legacy.Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter.value)
            }
        }
    }
}