package com.ivy.wallet.ui.reports

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.design.navigation.Navigation
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.data.entity.Category
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.logic.WalletLogic
import com.ivy.wallet.domain.logic.csv.ExportCSVLogic
import com.ivy.wallet.domain.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.logic.withDateDividers
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.paywall.PaywallReason
import com.ivy.wallet.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val settingsDao: SettingsDao,
    private val walletLogic: WalletLogic,
    private val transactionDao: TransactionDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val exportCSVLogic: ExportCSVLogic
) : ViewModel() {

    private val _period = MutableLiveData<TimePeriod>()
    val period = _period.asLiveData()

    private val _categories = MutableLiveData<List<Category>>()
    val categories = _categories.asLiveData()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    private val _baseCurrency = MutableLiveData<String>()
    val baseCurrency = _baseCurrency.asLiveData()

    private val _balance = MutableLiveData<Double>()
    val balance = _balance.asLiveData()

    private val _income = MutableLiveData<Double>()
    val income = _income.asLiveData()

    private val _expenses = MutableLiveData<Double>()
    val expenses = _expenses.asLiveData()

    //Upcoming
    private val _upcoming = MutableLiveData<List<Transaction>>()
    val upcoming = _upcoming.asLiveData()

    private val _upcomingIncome = MutableLiveData<Double>()
    val upcomingIncome = _upcomingIncome.asLiveData()

    private val _upcomingExpenses = MutableLiveData<Double>()
    val upcomingExpenses = _upcomingExpenses.asLiveData()

    private val _upcomingExpanded = MutableLiveData(false)
    val upcomingExpanded = _upcomingExpanded.asLiveData()

    //Overdue
    private val _overdue = MutableLiveData<List<Transaction>>()
    val overdue = _overdue.asLiveData()

    private val _overdueIncome = MutableLiveData<Double>()
    val overdueIncome = _overdueIncome.asLiveData()

    private val _overdueExpenses = MutableLiveData<Double>()
    val overdueExpenses = _overdueExpenses.asLiveData()

    private val _overdueExpanded = MutableLiveData(true)
    val overdueExpanded = _overdueExpanded.asLiveData()

    //History
    private val _history = MutableLiveData<List<TransactionHistoryItem>>()
    val history = _history.asLiveData()

    private val _filter = MutableLiveData<ReportFilter?>()
    val filter = _filter.asLiveData()

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading.asLiveData()

    private val _accountFilterList = MutableStateFlow<List<UUID>>(emptyList())
    val accountFilterList = _accountFilterList.readOnly()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.readOnly()

    fun start() {
        viewModelScope.launch {
            val baseCurrency = ioThread { settingsDao.findFirst().currency }
            _baseCurrency.value = baseCurrency

            val categories = ioThread { categoryDao.findAll() }
            _categories.value = categories

            val accounts = ioThread { accountDao.findAll() }
            _accounts.value = accounts
        }
    }

    fun setFilter(filter: ReportFilter?) {
        if (filter == null) {
            //clear filter
            _filter.value = null
            return
        }

        if (!filter.validate()) return

        val accounts = accounts.value ?: return
        val baseCurrency = baseCurrency.value ?: return

        viewModelScope.launch {
            _loading.value = true
            _filter.value = filter

            val transactions = ioThread {
                filterTransactions(
                    baseCurrency = baseCurrency,
                    accounts = accounts,
                    filter = filter
                )
            }

            val history = ioThread {
                transactions
                    .filter { it.dateTime != null }
                    .sortedByDescending { it.dateTime }
            }

            _transactions.value = history
            _history.value = ioThread {
                history.withDateDividers(
                    exchangeRatesLogic = exchangeRatesLogic,
                    settingsDao = settingsDao,
                    accountDao = accountDao
                )
            }!!
            val income = ioThread {
                walletLogic.calculateIncome(history)
            }
            _income.value = income
            val expenses = ioThread {
                walletLogic.calculateExpenses(history)
            }
            _expenses.value = expenses
            _balance.value = ioThread {
                calculateBalance(
                    baseCurrency = baseCurrency,
                    accounts = accounts,
                    history = history,
                    income = income,
                    expenses = expenses,
                    filter = filter
                )
            }!!

            val timeNowUTC = timeNowUTC()
            val upcoming = ioThread {
                transactions
                    .filter {
                        it.dueDate != null && it.dueDate.isAfter(timeNowUTC)
                    }
                    .sortedBy { it.dueDate }
            }
            _upcoming.value = upcoming
            _upcomingIncome.value = ioThread {
                walletLogic.calculateIncome(upcoming)
            }!!
            _upcomingExpenses.value = ioThread {
                walletLogic.calculateExpenses(upcoming)
            }!!

            val overdue = ioThread {
                transactions.filter {
                    it.dueDate != null && it.dueDate.isBefore(timeNowUTC)
                }.sortedByDescending {
                    it.dueDate
                }
            }
            _overdue.value = overdue
            _overdueIncome.value = ioThread {
                walletLogic.calculateIncome(overdue)
            }!!
            _overdueExpenses.value = ioThread {
                walletLogic.calculateExpenses(overdue)
            }!!

            _accountFilterList.value = computationThread {
                filter.accounts.map { it.id }
            }

            _loading.value = false
        }
    }

    private fun filterTransactions(
        baseCurrency: String,
        accounts: List<Account>,
        filter: ReportFilter,
    ): List<Transaction> {
        return transactionDao
            .findAll()
            .asSequence()
            .filter {
                //Filter by Transaction Type
                filter.trnTypes.contains(it.type)
            }
            .filter {
                //Filter by Time Period

                val filterRange = filter.period?.toRange(ivyContext.startDayOfMonth)
                    ?: return@filter false

                (it.dateTime != null && filterRange.includes(it.dateTime)) ||
                        (it.dueDate != null && filterRange.includes(it.dueDate))
            }
            .filter { trn ->
                //Filter by Accounts

                val filterAccountIds = filter.accounts.map { it.id }

                filterAccountIds.contains(trn.accountId) || //Transfers Out
                        (trn.toAccountId != null && filterAccountIds.contains(trn.toAccountId)) //Transfers In
            }
            .filter { trn ->
                //Filter by Categories
                val filterCategoryIds = filter.categories.map { it.id }

                filterCategoryIds.contains(trn.smartCategoryId())
            }
            .filter {
                //Filter by Amount
                //!NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeRatesLogic.amountBaseCurrency(
                    transaction = it,
                    baseCurrency = baseCurrency,
                    accounts = accounts
                )

                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
            }
            .filter {
                //Filter by Included Keywords

                val includeKeywords = filter.includeKeywords
                if (includeKeywords.isEmpty()) return@filter true

                if (it.title != null && it.title.isNotEmpty()) {
                    includeKeywords.forEach { keyword ->
                        if (it.title.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                if (it.description != null && it.description.isNotEmpty()) {
                    includeKeywords.forEach { keyword ->
                        if (it.description.containsLowercase(keyword)) {
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

                if (it.title != null && it.title.isNotEmpty()) {
                    excludedKeywords.forEach { keyword ->
                        if (it.title.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }

                if (it.description != null && it.description.isNotEmpty()) {
                    excludedKeywords.forEach { keyword ->
                        if (it.description.containsLowercase(keyword)) {
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

    private fun calculateBalance(
        baseCurrency: String,
        accounts: List<Account>,
        history: List<Transaction>,
        income: Double,
        expenses: Double,
        filter: ReportFilter
    ): Double {
        val includedAccountsIds = filter.accounts.map { it.id }
        //+ Transfers In (#conv to BaseCurrency)
        val transfersIn = history
            .filter {
                it.type == TransactionType.TRANSFER &&
                        it.toAccountId != null && includedAccountsIds.contains(it.toAccountId)
            }
            .sumOf { trn ->
                exchangeRatesLogic.toAmountBaseCurrency(
                    transaction = trn,
                    baseCurrency = baseCurrency,
                    accounts = accounts
                )
            }

        //- Transfers Out (#conv to BaseCurrency)
        val transfersOut = history
            .filter {
                it.type == TransactionType.TRANSFER &&
                        includedAccountsIds.contains(it.accountId)
            }
            .sumOf { trn ->
                exchangeRatesLogic.amountBaseCurrency(
                    transaction = trn,
                    baseCurrency = baseCurrency,
                    accounts = accounts
                )
            }

        //Income - Expenses (#conv to BaseCurrency)
        return income - expenses + transfersIn - transfersOut
    }

    fun export(context: Context) {
        ivyContext.protectWithPaywall(
            paywallReason = PaywallReason.EXPORT_CSV,
            navigation = nav
        ) {
            val filter = _filter.value ?: return@protectWithPaywall
            if (!filter.validate()) return@protectWithPaywall
            val accounts = _accounts.value ?: return@protectWithPaywall
            val baseCurrency = _baseCurrency.value ?: return@protectWithPaywall

            ivyContext.createNewFile(
                "Report (${
                    timeNowUTC().formatNicelyWithTime(noWeekDay = true)
                }).csv"
            ) { fileUri ->
                viewModelScope.launch {
                    _loading.value = true

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

                    (context as RootActivity).shareCSVFile(
                        fileUri = fileUri
                    )

                    _loading.value = false
                }
            }
        }
    }

    fun setUpcomingExpanded(expanded: Boolean) {
        _upcomingExpanded.value = expanded
    }

    fun setOverdueExpanded(expanded: Boolean) {
        _overdueExpanded.value = expanded
    }


    fun payOrGet(transaction: Transaction) {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start()
            }
        }
    }
}