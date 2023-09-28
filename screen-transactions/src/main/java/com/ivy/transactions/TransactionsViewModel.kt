package com.ivy.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import arrow.core.toOption
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.util.stringRes
import com.ivy.domain.ComposeViewModel
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.then
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.computationThread
import com.ivy.legacy.utils.dateNowUTC
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.selectEndTextFieldValue
import com.ivy.navigation.ItemStatisticScreen
import com.ivy.navigation.Navigation
import com.ivy.persistence.db.dao.read.AccountDao
import com.ivy.persistence.db.dao.read.CategoryDao
import com.ivy.persistence.db.dao.write.WriteAccountDao
import com.ivy.persistence.db.dao.write.WriteCategoryDao
import com.ivy.persistence.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.persistence.db.dao.write.WriteTransactionDao
import com.ivy.persistence.model.TransactionType
import com.ivy.resources.R
import com.ivy.wallet.domain.action.account.AccTrnsAct
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.account.CalcAccBalanceAct
import com.ivy.wallet.domain.action.account.CalcAccIncomeExpenseAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.WalletAccountLogic
import com.ivy.wallet.domain.deprecated.logic.WalletCategoryLogic
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.ui.theme.RedLight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val accountLogic: WalletAccountLogic,
    private val categoryLogic: WalletCategoryLogic,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val sharedPrefs: SharedPrefs,
    private val categoriesAct: CategoriesAct,
    private val accountsAct: AccountsAct,
    private val accTrnsAct: AccTrnsAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val calcAccBalanceAct: CalcAccBalanceAct,
    private val calcAccIncomeExpenseAct: CalcAccIncomeExpenseAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val exchangeAct: ExchangeAct,
    private val transactionWriter: WriteTransactionDao,
    private val categoryWriter: WriteCategoryDao,
    private val accountWriter: WriteAccountDao,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
) : ComposeViewModel<TransactionsState, TransactionsEvent>() {

    private val period = mutableStateOf(ivyContext.selectedPeriod)
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val baseCurrency = mutableStateOf("")
    private val currency = mutableStateOf("")
    private val balance = mutableDoubleStateOf(0.0)
    private val balanceBaseCurrency = mutableStateOf<Double?>(null)
    private val income = mutableDoubleStateOf(0.0)
    private val expenses = mutableDoubleStateOf(0.0)

    // Upcoming
    private val upcoming = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val upcomingIncome = mutableDoubleStateOf(0.0)
    private val upcomingExpenses = mutableDoubleStateOf(0.0)
    private val upcomingExpanded = mutableStateOf(false)

    // Overdue
    private val overdue = mutableStateOf<ImmutableList<Transaction>>(persistentListOf())
    private val overdueIncome = mutableDoubleStateOf(0.0)
    private val overdueExpenses = mutableDoubleStateOf(0.0)
    private val overdueExpanded = mutableStateOf(true)

    // History
    private val history =
        mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())

    private val account = mutableStateOf<Account?>(null)
    private val category = mutableStateOf<Category?>(null)
    private val initWithTransactions = mutableStateOf(false)
    private val treatTransfersAsIncomeExpense = mutableStateOf(false)

    var accountNameConfirmation by mutableStateOf(selectEndTextFieldValue(""))
        private set
    var enableDeletionButton by mutableStateOf(false)
        private set

    @Composable
    override fun uiState(): TransactionsState {
        return TransactionsState()
    }

    fun start(
        screen: ItemStatisticScreen,
        period: TimePeriod? = ivyContext.selectedPeriod,
        reset: Boolean = true
    ) {
        TestIdlingResource.increment()

        if (reset) {
            reset()
        }

        viewModelScope.launch {
            _period.value = period ?: ivyContext.selectedPeriod!!

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
                // unspecifiedCategory==false is explicitly checked to accommodate for a temp AccountTransfers Category during Reports Screen
                screen.categoryId != null && screen.transactions.isNotEmpty() &&
                        screen.unspecifiedCategory == false -> {
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

        _history.value = (
                accTrnsAct then {
                    trnsWithDateDivsAct(
                        TrnsWithDateDivsAct.Input(
                            baseCurrency = baseCurrency.value,
                            transactions = it
                        )
                    )
                }
                )(
            AccTrnsAct.Input(
                accountId = account.id,
                range = range.toCloseTimeRange()
            )
        ).toImmutableList()

        // Upcoming
        _upcomingIncome.value = ioThread {
            accountLogic.calculateUpcomingIncome(account, range)
        }

        _upcomingExpenses.value = ioThread {
            accountLogic.calculateUpcomingExpenses(account, range)
        }

        _upcoming.value = ioThread { accountLogic.upcoming(account, range).toImmutableList() }

        // Overdue
        _overdueIncome.value = ioThread {
            accountLogic.calculateOverdueIncome(account, range)
        }

        _overdueExpenses.value = ioThread {
            accountLogic.calculateOverdueExpenses(account, range)
        }

        _overdue.value = ioThread { accountLogic.overdue(account, range).toImmutableList() }
    }

    private suspend fun initForCategory(categoryId: UUID, accountFilterList: List<UUID>) {
        val accountFilterSet = accountFilterList.toSet()
        val category = ioThread {
            categoryDao.findById(categoryId)?.toDomain() ?: error("category not found")
        }
        _category.value = category
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
            ).toImmutableList()
        }

        // Upcoming
        // TODO: Rework Upcoming to FP
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeByCategory(category, range)
        }

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesByCategory(category, range)
        }

        _upcoming.value = ioThread {
            categoryLogic.upcomingByCategory(category, range).toImmutableList()
        }

        // Overdue
        // TODO: Rework Overdue to FP
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeByCategory(category, range)
        }

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesByCategory(category, range)
        }

        _overdue.value =
            ioThread { categoryLogic.overdueByCategory(category, range).toImmutableList() }
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
                ).toImmutableList()
            }

            // Upcoming
            // TODO: Rework Upcoming to FP
            _upcomingIncome.value = ioThread {
                categoryLogic.calculateUpcomingIncomeByCategory(category, range)
            }

            _upcomingExpenses.value = ioThread {
                categoryLogic.calculateUpcomingExpensesByCategory(category, range)
            }

            _upcoming.value = ioThread {
                categoryLogic.upcomingByCategory(category, range).toImmutableList()
            }

            // Overdue
            // TODO: Rework Overdue to FP
            _overdueIncome.value = ioThread {
                categoryLogic.calculateOverdueIncomeByCategory(category, range)
            }

            _overdueExpenses.value = ioThread {
                categoryLogic.calculateOverdueExpensesByCategory(category, range)
            }

            _overdue.value =
                ioThread { categoryLogic.overdueByCategory(category, range).toImmutableList() }
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
            categoryLogic.historyUnspecified(range).toImmutableList()
        }

        // Upcoming
        _upcomingIncome.value = ioThread {
            categoryLogic.calculateUpcomingIncomeUnspecified(range)
        }

        _upcomingExpenses.value = ioThread {
            categoryLogic.calculateUpcomingExpensesUnspecified(range)
        }

        _upcoming.value = ioThread {
            categoryLogic.upcomingUnspecified(range).toImmutableList()
        }

        // Overdue
        _overdueIncome.value = ioThread {
            categoryLogic.calculateOverdueIncomeUnspecified(range)
        }

        _overdueExpenses.value = ioThread {
            categoryLogic.calculateOverdueExpensesUnspecified(range)
        }

        _overdue.value = ioThread { categoryLogic.overdueUnspecified(range).toImmutableList() }
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
            it.categoryId == null && (
                    accountFilterIdSet.contains(it.accountId) || accountFilterIdSet.contains(
                        it.toAccountId
                    )
                    ) && it.type == TransactionType.TRANSFER
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
        ).toImmutableList()
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
        screen: ItemStatisticScreen,
        period: TimePeriod
    ) {
        start(
            screen = screen,
            period = period,
            reset = false
        )
    }

    fun nextMonth(screen: ItemStatisticScreen) {
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

    fun previousMonth(screen: ItemStatisticScreen) {
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

    fun delete(screen: ItemStatisticScreen) {
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
            transactionWriter.flagDeletedByAccountId(accountId = accountId)
            plannedPaymentRuleWriter.flagDeletedByAccountId(accountId = accountId)
            accountWriter.flagDeleted(accountId)

            nav.back()
        }
    }

    private suspend fun deleteCategory(categoryId: UUID) {
        ioThread {
            categoryWriter.flagDeleted(categoryId)

            nav.back()
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

    fun editAccount(screen: ItemStatisticScreen, account: Account, newBalance: Double) {
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

    fun payOrGet(screen: ItemStatisticScreen, transaction: Transaction) {
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

    fun skipTransaction(screen: ItemStatisticScreen, transaction: Transaction) {
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

    fun skipTransactions(screen: ItemStatisticScreen, transactions: List<Transaction>) {
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

    fun updateAccountDeletionState(newName: String) {
        accountNameConfirmation = selectEndTextFieldValue(newName)
        account.value?.name?.let { accountName ->
            enableDeletionButton = newName == accountName
        }
    }

    override fun onEvent(event: TransactionsEvent) {
        TODO("Not yet implemented")
    }
}