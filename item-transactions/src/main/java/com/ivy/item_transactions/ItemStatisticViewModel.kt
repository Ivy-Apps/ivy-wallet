package com.ivy.item_transactions

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.toOption
import com.ivy.base.R
import com.ivy.base.TimePeriod
import com.ivy.base.stringRes
import com.ivy.base.toCloseTimeRange
import com.ivy.data.Account
import com.ivy.data.Category
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.exchange.ExchangeAct
import com.ivy.exchange.ExchangeData
import com.ivy.exchange.ExchangeRateDao
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.then
import com.ivy.frp.view.navigation.Navigation
import com.ivy.screens.ItemStatistic
import com.ivy.wallet.domain.action.account.AccTrnsAct
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.account.CalcAccIncomeExpenseAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.*
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.sync.uploader.AccountUploader
import com.ivy.wallet.domain.deprecated.sync.uploader.CategoryUploader
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.ui.theme.RedLight
import com.ivy.wallet.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemStatisticViewModel @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val accountDao: AccountDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val ivyContext: com.ivy.base.IvyWalletCtx,
    private val nav: Navigation,
    private val categoryUploader: CategoryUploader,
    private val accountUploader: AccountUploader,
    private val accountLogic: WalletAccountLogic,
    private val categoryLogic: WalletCategoryLogic,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val sharedPrefs: SharedPrefs,
    private val categoriesAct: CategoriesAct,
    private val accountsAct: AccountsAct,
    private val accTrnsAct: AccTrnsAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val exchangeAct: ExchangeAct
) : ViewModel() {

    private val _period = MutableStateFlow(ivyContext.selectedPeriod)
    val period = _period.readOnly()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.readOnly()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.readOnly()

    private val _baseCurrency = MutableStateFlow("")
    val baseCurrency = _baseCurrency.readOnly()

    private val _currency = MutableStateFlow("")
    val currency = _currency.readOnly()

    private val _balance = MutableStateFlow(0.0)
    val balance = _balance.readOnly()

    private val _balanceBaseCurrency = MutableStateFlow<Double?>(null)
    val balanceBaseCurrency = _balanceBaseCurrency.readOnly()

    private val _income = MutableStateFlow(0.0)
    val income = _income.readOnly()

    private val _expenses = MutableStateFlow(0.0)
    val expenses = _expenses.readOnly()

    //Upcoming
    private val _upcoming = MutableStateFlow<List<Transaction>>(emptyList())
    val upcoming = _upcoming.readOnly()

    private val _upcomingIncome = MutableStateFlow(0.0)
    val upcomingIncome = _upcomingIncome.readOnly()

    private val _upcomingExpenses = MutableStateFlow(0.0)
    val upcomingExpenses = _upcomingExpenses.readOnly()

    private val _upcomingExpanded = MutableStateFlow(false)
    val upcomingExpanded = _upcomingExpanded.readOnly()

    //Overdue
    private val _overdue = MutableStateFlow<List<Transaction>>(emptyList())
    val overdue = _overdue.readOnly()

    private val _overdueIncome = MutableStateFlow(0.0)
    val overdueIncome = _overdueIncome.readOnly()

    private val _overdueExpenses = MutableStateFlow(0.0)
    val overdueExpenses = _overdueExpenses.readOnly()

    private val _overdueExpanded = MutableStateFlow(true)
    val overdueExpanded = _overdueExpanded.readOnly()

    //History
    private val _history = MutableStateFlow<List<Any>>(emptyList())
    val history = _history.readOnly()

    private val _account = MutableStateFlow<Account?>(null)
    val account = _account.readOnly()

    private val _category = MutableStateFlow<Category?>(null)
    val category = _category.readOnly()

    private val _isParentCategory = MutableStateFlow(false)
    val isParentCategory = _isParentCategory.readOnly()

    private val _parentCategoryList = MutableStateFlow<List<Category>>(emptyList())
    val parentCategoryList = _parentCategoryList.readOnly()

    private val _initWithTransactions = MutableStateFlow(false)
    val initWithTransactions = _initWithTransactions.readOnly()

    private val _treatTransfersAsIncomeExpense = MutableStateFlow(false)
    val treatTransfersAsIncomeExpense = _treatTransfersAsIncomeExpense.readOnly()

    fun start(
        screen: ItemStatistic,
        period: TimePeriod? = ivyContext.selectedPeriod,
        reset: Boolean = true
    ) {
        TestIdlingResource.increment()

        if (reset) {
            reset()
        }

        viewModelScope.launch {
            _period.value = period ?: ivyContext.selectedPeriod

            val baseCurrency = baseCurrencyAct(Unit)
            _baseCurrency.value = baseCurrency
            _currency.value = baseCurrency

            _categories.value = categoriesAct(Unit)
            _accounts.value = accountsAct(Unit)
            _initWithTransactions.value = false
            _treatTransfersAsIncomeExpense.value =
                sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

            when {
                screen.accountId != null -> {
                    initForAccount(screen.accountId!!)
                }
                screen.categoryId != null && screen.transactions.isEmpty() -> {
                    initForCategory(screen.categoryId!!, screen.accountIdFilterList)
                }
                //unspecifiedCategory==false is explicitly checked to accommodate for a temp AccountTransfers Category during Reports Screen
                screen.categoryId != null && screen.transactions.isNotEmpty()
                        && screen.unspecifiedCategory == false -> {
                    initForCategoryWithTransactions(
                        screen.categoryId!!,
                        screen.accountIdFilterList,
                        screen.transactions
                    )
                }
                screen.unspecifiedCategory == true && screen.transactions.isNotEmpty() -> {
                    initForAccountTransfersCategory(
                        screen.categoryId,
                        screen.accountIdFilterList,
                        screen.transactions
                    )
                }
                screen.unspecifiedCategory == true -> {
                    initForUnspecifiedCategory()
                }
                else -> error("no id provided")
            }
        }

        TestIdlingResource.decrement()
    }

    private suspend fun initForAccount(accountId: UUID) {
        val account = ioThread {
            accountDao.findById(accountId)?.toDomain() ?: error("account not found")
        }
        _account.value = account
        val range = period.value.toRange(ivyContext.startDayOfMonth)

        if (account.currency.isNotNullOrBlank()) {
            _currency.value = account.currency!!
        }

        val balance = calcAccBalanceAct(
            CalcAccBalanceAct.Input(
                account = account
            )
        ).balance.toDouble()
        _balance.value = balance
        if (baseCurrency.value != currency.value) {
            _balanceBaseCurrency.value = exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency.value,
                        fromCurrency = currency.value.toOption()
                    ),
                    amount = balance.toBigDecimal()
                )
            ).orNull()?.toDouble()
        }

        val includeTransfersInCalc =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

        val incomeExpensePair = calcAccIncomeExpenseAct(
            CalcAccIncomeExpenseAct.Input(
                account = account,
                range = range.toCloseTimeRange(),
                includeTransfersInCalc = includeTransfersInCalc
            )
        ).incomeExpensePair
        _income.value = incomeExpensePair.income.toDouble()
        _expenses.value = incomeExpensePair.expense.toDouble()

        _history.value = (accTrnsAct then {
            trnsWithDateDivsAct(
                TrnsWithDateDivsAct.Input(
                    baseCurrency = baseCurrency.value,
                    transactions = it
                )
            )
        })(
            AccTrnsAct.Input(
                accountId = account.id,
                range = range.toCloseTimeRange()
            )
        )

        //Upcoming
        _upcomingIncome.value = ioThread {
            accountLogic.calculateUpcomingIncome(account, range)
        }

        _upcomingExpenses.value = ioThread {
            accountLogic.calculateUpcomingExpenses(account, range)
        }

        _upcoming.value = ioThread { accountLogic.upcoming(account, range) }

        //Overdue
        _overdueIncome.value = ioThread {
            accountLogic.calculateOverdueIncome(account, range)
        }

        _overdueExpenses.value = ioThread {
            accountLogic.calculateOverdueExpenses(account, range)
        }

        _overdue.value = ioThread { accountLogic.overdue(account, range) }
    }

    private suspend fun initForCategory(categoryId: UUID, accountFilterList: List<UUID>) {
        val accountFilterSet = accountFilterList.toSet()
        val category = ioThread {
            categoryDao.findById(categoryId)?.toDomain() ?: error("category not found")
        }
        _category.value = category
        _isParentCategory.value =
            ioThread { categoryDao.findAllSubCategories(category.id).isNotEmpty() }

        _parentCategoryList.value =
            ioThread { categoryDao.findAllParentCategories().map { it.toDomain() } }

        val range = period.value.toRange(ivyContext.startDayOfMonth)

        _balance.value = ioThread {
            categoryLogic.calculateCategoryBalance(category, range, accountFilterSet)
        }

        _income.value = ioThread {
            categoryLogic.calculateCategoryIncome(category, range, accountFilterSet)
        }

        _expenses.value = ioThread {
            categoryLogic.calculateCategoryExpenses(category, range, accountFilterSet)
        }

        _history.value = ioThread {
            categoryLogic.historyByCategoryAccountWithDateDividers(
                category,
                range,
                accountFilterSet = accountFilterList.toSet(),
            )
        }

        //Upcoming
        //TODO: Rework Upcoming to FP
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeByCategory(category, range)
        }

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesByCategory(category, range)
        }

        _upcoming.value = ioThread { categoryLogic.upcomingByCategory(category, range) }

        //Overdue
        //TODO: Rework Overdue to FP
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeByCategory(category, range)
        }

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesByCategory(category, range)
        }

        _overdue.value = ioThread { categoryLogic.overdueByCategory(category, range) }
    }

    private suspend fun initForCategoryWithTransactions(
        categoryId: UUID,
        accountFilterList: List<UUID>,
        transactions: List<Transaction>
    ) {
        computationThread {
            _initWithTransactions.value = true

            val trans = transactions.filter {
                it.type != TransactionType.TRANSFER && it.categoryId == categoryId
            }

            val accountFilterSet = accountFilterList.toSet()
            val category = ioThread {
                categoryDao.findById(categoryId)?.toDomain() ?: error("category not found")
            }
            _category.value = category
            val range = period.value.toRange(ivyContext.startDayOfMonth)

            val incomeTrans = transactions.filter {
                it.categoryId == categoryId && it.type == TransactionType.INCOME
            }

            val expenseTrans = transactions.filter {
                it.categoryId == categoryId && it.type == TransactionType.EXPENSE
            }

            _balance.value = ioThread {
                categoryLogic.calculateCategoryBalance(
                    category,
                    range,
                    accountFilterSet,
                    transactions = trans
                )
            }

            _income.value = ioThread {
                categoryLogic.calculateCategoryIncome(
                    incomeTransaction = incomeTrans,
                    accountFilterSet = accountFilterSet
                )
            }

            _expenses.value = ioThread {
                categoryLogic.calculateCategoryExpenses(
                    expenseTransactions = expenseTrans,
                    accountFilterSet = accountFilterSet
                )
            }

            _history.value = ioThread {
                categoryLogic.historyByCategoryAccountWithDateDividers(
                    category,
                    range,
                    accountFilterSet = accountFilterList.toSet(),
                    transactions = trans
                )
            }

            //Upcoming
            //TODO: Rework Upcoming to FP
            _upcomingIncome.value = ioThread {
                categoryLogic.calculateUpcomingIncomeByCategory(category, range)
            }

            _upcomingExpenses.value = ioThread {
                categoryLogic.calculateUpcomingExpensesByCategory(category, range)
            }

            _upcoming.value = ioThread { categoryLogic.upcomingByCategory(category, range) }

            //Overdue
            //TODO: Rework Overdue to FP
            _overdueIncome.value = ioThread {
                categoryLogic.calculateOverdueIncomeByCategory(category, range)
            }

            _overdueExpenses.value = ioThread {
                categoryLogic.calculateOverdueExpensesByCategory(category, range)
            }

            _overdue.value = ioThread { categoryLogic.overdueByCategory(category, range) }
        }
    }

    private suspend fun initForUnspecifiedCategory() {
        val range = period.value.toRange(ivyContext.startDayOfMonth)

        _balance.value = ioThread {
            categoryLogic.calculateUnspecifiedBalance(range)
        }

        _income.value = ioThread {
            categoryLogic.calculateUnspecifiedIncome(range)
        }

        _expenses.value = ioThread {
            categoryLogic.calculateUnspecifiedExpenses(range)
        }

        _history.value = ioThread {
            categoryLogic.historyUnspecified(range)
        }

        //Upcoming
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeUnspecified(range)
        }

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesUnspecified(range)
        }

        _upcoming.value = ioThread { categoryLogic.upcomingUnspecified(range) }

        //Overdue
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeUnspecified(range)
        }

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesUnspecified(range)
        }

        _overdue.value = ioThread { categoryLogic.overdueUnspecified(range) }
    }

    private suspend fun initForAccountTransfersCategory(
        categoryId: UUID?,
        accountFilterList: List<UUID>,
        transactions: List<Transaction>
    ) {
        _initWithTransactions.value = true
        _category.value =
            Category(stringRes(R.string.account_transfers), RedLight.toArgb(), "transfer")
        val accountFilterIdSet = accountFilterList.toHashSet()
        val trans = transactions.filter {
            it.categoryId == null && (accountFilterIdSet.contains(it.accountId) || accountFilterIdSet.contains(
                it.toAccountId
            )) && it.type == TransactionType.TRANSFER
        }

        val historyIncomeExpense = calcTrnsIncomeExpenseAct(
            CalcTrnsIncomeExpenseAct.Input(
                transactions = trans,
                accounts = accountFilterList.mapNotNull { accID -> accounts.value.find { it.id == accID } },
                baseCurrency = baseCurrency.value
            )
        )

        _income.value = historyIncomeExpense.transferIncome.toDouble()
        _expenses.value = historyIncomeExpense.transferExpense.toDouble()
        _balance.value = _income.value - _expenses.value
        _history.value = trnsWithDateDivsAct(
            TrnsWithDateDivsAct.Input(
                baseCurrency = baseCurrency.value,
                transactions = transactions
            )
        )
    }

    private fun reset() {
        _account.value = null
        _category.value = null
    }

    fun setUpcomingExpanded(expanded: Boolean) {
        _upcomingExpanded.value = expanded
    }

    fun setOverdueExpanded(expanded: Boolean) {
        _overdueExpanded.value = expanded
    }

    fun setPeriod(
        screen: ItemStatistic,
        period: TimePeriod
    ) {
        start(
            screen = screen,
            period = period,
            reset = false
        )
    }

    fun nextMonth(screen: ItemStatistic) {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                period = month.incrementMonthPeriod(ivyContext, 1L, year),
                reset = false
            )
        }
    }

    fun previousMonth(screen: ItemStatistic) {
        val month = period.value.month
        val year = period.value.year ?: dateNowUTC().year
        if (month != null) {
            start(
                screen = screen,
                period = month.incrementMonthPeriod(ivyContext, -1L, year),
                reset = false
            )
        }
    }

    fun delete(screen: ItemStatistic) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            when {
                screen.accountId != null -> {
                    deleteAccount(screen.accountId!!)
                }
                screen.categoryId != null -> {
                    deleteCategory(screen.categoryId!!)
                }
            }

            TestIdlingResource.decrement()
        }
    }

    private suspend fun deleteAccount(accountId: UUID) {
        ioThread {
            transactionDao.flagDeletedByAccountId(accountId = accountId)
            plannedPaymentRuleDao.flagDeletedByAccountId(accountId = accountId)
            accountDao.flagDeleted(accountId)

            nav.back()

            //the server deletes transactions + planned payments for the account
            accountUploader.delete(accountId)
        }
    }

    private suspend fun deleteCategory(categoryId: UUID) {
        ioThread {
            categoryDao.flagDeleted(categoryId)

            nav.back()

            categoryUploader.delete(categoryId)
        }
    }

    fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            categoryCreator.editCategory(updatedCategory) {
                _category.value = it
            }

            TestIdlingResource.decrement()
        }
    }

    fun editAccount(screen: ItemStatistic, account: Account, newBalance: Double) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.editAccount(account, newBalance) {
                start(
                    screen = screen,
                    period = period.value,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun payOrGet(screen: ItemStatistic, transaction: Transaction) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(transaction = transaction) {
                start(
                    screen = screen,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun skipTransaction(screen: ItemStatistic, transaction: Transaction) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(
                transaction = transaction,
                skipTransaction = true
            ) {
                start(
                    screen = screen,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun skipTransactions(screen: ItemStatistic, transactions: List<Transaction>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(
                transactions = transactions,
                skipTransaction = true
            ) {
                start(
                    screen = screen,
                    reset = false
                )
            }

            TestIdlingResource.decrement()
        }
    }
}