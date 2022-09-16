package com.ivy.reports

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivy.base.R
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.filterSuspend
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.frp.viewmodel.readOnly
import com.ivy.temp.persistence.ExchangeActOld
import com.ivy.temp.persistence.ExchangeData
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.action.settings.BaseCurrencyActOld
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import com.ivy.wallet.domain.pure.util.orZero
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val settingsDao: SettingsDao,
    private val transactionDao: TransactionDao,
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val nav: Navigation,
    private val exportCSVLogic: ExportCSVLogic,
    private val exchangeAct: ExchangeActOld,
    private val accountsAct: AccountsActOld,
    private val categoriesAct: CategoriesActOld,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val baseCurrencyAct: BaseCurrencyActOld
) : FRPViewModel<ReportScreenState, Nothing>() {
    override val _state: MutableStateFlow<ReportScreenState> = MutableStateFlow(
        ReportScreenState()
    )

    override suspend fun handleEvent(event: Nothing): suspend () -> ReportScreenState {
        TODO("Not yet implemented")
    }

    private val unSpecifiedCategory =
        CategoryOld(com.ivy.core.ui.temp.stringRes(R.string.unspecified), color = Gray.toArgb())

    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _categories = MutableStateFlow<List<CategoryOld>>(emptyList())
    val categories = _categories.readOnly()

    private val _allAccounts = MutableStateFlow<List<AccountOld>>(emptyList())

    private val _baseCurrency = MutableStateFlow("")
    val baseCurrency = _baseCurrency.readOnly()

    private val _historyIncomeExpense = MutableStateFlow(IncomeExpenseTransferPair.zero())
    private val historyIncomeExpense = _historyIncomeExpense.readOnly()

    private val _filter = MutableStateFlow<ReportFilter?>(null)
    val filter = _filter.readOnly()

    fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            _baseCurrency.value = baseCurrencyAct(Unit)
            _allAccounts.value = accountsAct(Unit)
            _categories.value = listOf(unSpecifiedCategory) + categoriesAct(Unit)

            updateState {
                it.copy(
                    baseCurrency = _baseCurrency.value,
                    categories = _categories.value,
                    accounts = _allAccounts.value
                )
            }
        }
    }

    private suspend fun setFilter(filter: ReportFilter?) {
        scopedIOThread { scope ->
            if (filter == null) {
                //clear filter
                _filter.value = null
                return@scopedIOThread
            }

            if (!filter.validate()) return@scopedIOThread
            val accounts = filter.accounts
            val baseCurrency = baseCurrency.value
            _filter.value = filter

            updateState {
                it.copy(loading = true, filter = _filter.value)
            }

            val transactions = filterTransactions(
                baseCurrency = baseCurrency,
                accounts = accounts,
                filter = filter
            )

            val history = transactions
                .filter { it.dateTime != null }
                .sortedByDescending { it.dateTime }

            val historyWithDateDividers = scope.async {
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = stateVal().baseCurrency,
                        transactions = history
                    )
                )
            }

            _historyIncomeExpense.value = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = history,
                    accounts = accounts,
                    baseCurrency = baseCurrency
                )
            )

            val income = historyIncomeExpense.value.income.toDouble() +
                    if (stateVal().treatTransfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0

            val expenses = historyIncomeExpense.value.expense.toDouble() +
                    if (stateVal().treatTransfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0

            val balance = calculateBalance(historyIncomeExpense.value).toDouble()

            val accountFilterIdList = scope.async { filter.accounts.map { it.id } }

            val timeNowUTC = timeNowUTC()

            //Upcoming
            val upcomingTransactions = transactions
                .filter {
                    it.dueDate != null && it.dueDate!!.isAfter(timeNowUTC)
                }
                .sortedBy { it.dueDate }

            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = upcomingTransactions,
                    accounts = accounts,
                    baseCurrency = baseCurrency
                )
            )
            //Overdue
            val overdue = transactions.filter {
                it.dueDate != null && it.dueDate!!.isBefore(timeNowUTC)
            }.sortedByDescending {
                it.dueDate
            }
            val overdueIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = overdue,
                    accounts = accounts,
                    baseCurrency = baseCurrency
                )
            )

            updateState {
                it.copy(
                    income = income,
                    expenses = expenses,
                    upcomingIncome = upcomingIncomeExpense.income.toDouble(),
                    upcomingExpenses = upcomingIncomeExpense.expense.toDouble(),
                    overdueIncome = overdueIncomeExpense.income.toDouble(),
                    overdueExpenses = overdueIncomeExpense.expense.toDouble(),
                    history = historyWithDateDividers.await(),
                    upcomingTransactions = upcomingTransactions,
                    overdueTransactions = overdue,
                    categories = categories.value,
                    accounts = _allAccounts.value,
                    filter = filter,
                    loading = false,
                    accountIdFilters = accountFilterIdList.await(),
                    transactions = transactions,
                    balance = balance,
                    filterOverlayVisible = false,
                    showTransfersAsIncExpCheckbox = filter.trnTypes.contains(TrnTypeOld.TRANSFER)
                )
            }
        }
    }

    private suspend fun filterTransactions(
        baseCurrency: String,
        accounts: List<AccountOld>,
        filter: ReportFilter,
    ): List<TransactionOld> {
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

                filterCategoryIds.contains(trn.categoryId) || (trn.type == TrnTypeOld.TRANSFER)
            }
            .filterSuspend {
                //Filter by Amount
                //!NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeAct(
                    ExchangeActOld.Input(
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
            .toList()
    }

    private fun String.containsLowercase(anotherString: String): Boolean {
        return this.toLowerCaseLocal().contains(anotherString.toLowerCaseLocal())
    }

    private fun calculateBalance(incomeExpenseTransferPair: IncomeExpenseTransferPair): BigDecimal {
        return incomeExpenseTransferPair.income + incomeExpenseTransferPair.transferIncome - incomeExpenseTransferPair.expense - incomeExpenseTransferPair.transferExpense
    }

    private suspend fun export(context: Context) {
        val filter = _filter.value ?: return
        if (!filter.validate()) return
        val accounts = _allAccounts.value
        val baseCurrency = _baseCurrency.value

        ivyContext.createNewFile(
            "Report (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).csv"
        ) { fileUri ->
            viewModelScope.launch {
                updateState {
                    it.copy(loading = true)
                }

                exportCSVLogic.exportToFile(
                    context = context,
                    fileUri = fileUri,
                    exportScope = {
                        filterTransactions(
                            baseCurrency = baseCurrency,
                            accounts = accounts,
                            filter = filter
                        )
                    }
                )

                (context as com.ivy.core.ui.temp.RootScreen).shareCSVFile(
                    fileUri = fileUri
                )

                updateState {
                    it.copy(loading = false)
                }
            }
        }
    }

    private fun setUpcomingExpanded(expanded: Boolean) {
        updateStateNonBlocking {
            it.copy(upcomingExpanded = expanded)
        }
    }

    private fun setOverdueExpanded(expanded: Boolean) {
        updateStateNonBlocking {
            it.copy(overdueExpanded = expanded)
        }
    }

    private suspend fun payOrGet(transaction: TransactionOld) {
        uiThread {
            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start()
            }
        }
    }

    private fun setFilterOverlayVisible(filterOverlayVisible: Boolean) {
        updateStateNonBlocking {
            it.copy(filterOverlayVisible = filterOverlayVisible)
        }
    }

    private suspend fun onTreatTransfersAsIncomeExpense(treatTransfersAsIncExp: Boolean) {
        updateState {
            val income = historyIncomeExpense.value.income.toDouble() +
                    if (treatTransfersAsIncExp) historyIncomeExpense.value.transferIncome.toDouble() else 0.0
            val expenses = historyIncomeExpense.value.expense.toDouble() +
                    if (treatTransfersAsIncExp) historyIncomeExpense.value.transferExpense.toDouble() else 0.0
            it.copy(
                treatTransfersAsIncExp = treatTransfersAsIncExp,
                income = income,
                expenses = expenses
            )
        }
    }

    fun onEvent(event: ReportScreenEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is ReportScreenEvent.OnFilter -> setFilter(event.filter)
                is ReportScreenEvent.OnExport -> export(event.context)
                is ReportScreenEvent.OnPayOrGet -> payOrGet(event.transaction)
                is ReportScreenEvent.OnOverdueExpanded -> setOverdueExpanded(event.overdueExpanded)
                is ReportScreenEvent.OnUpcomingExpanded -> setUpcomingExpanded(event.upcomingExpanded)
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisible(event.filterOverlayVisible)
                is ReportScreenEvent.OnTreatTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
                    event.transfersAsIncomeExpense
                )
            }
        }
    }
}