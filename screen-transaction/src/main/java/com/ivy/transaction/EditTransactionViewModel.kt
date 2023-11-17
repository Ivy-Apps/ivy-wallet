package com.ivy.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.refreshWidget
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.domain.ComposeViewModel
import com.ivy.domain.event.AccountUpdatedEvent
import com.ivy.domain.event.EventBus
import com.ivy.legacy.data.EditTransactionDisplayLoan
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.toEntity
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.computationThread
import com.ivy.legacy.utils.dateNowLocal
import com.ivy.legacy.utils.getTrueDate
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.timeUTC
import com.ivy.legacy.utils.uiThread
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.action.account.AccountByIdAct
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryByIdAct
import com.ivy.wallet.domain.action.transaction.TrnByIdAct
import com.ivy.wallet.domain.data.CustomExchangeRateState
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.deprecated.logic.SmartTitleSuggestionsLogic
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.widget.balance.WalletBalanceWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val loanDao: LoanDao,
    private val settingsDao: SettingsDao,
    private val nav: Navigation,
    private val sharedPrefs: SharedPrefs,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val smartTitleSuggestionsLogic: SmartTitleSuggestionsLogic,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val trnByIdAct: TrnByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val accountByIdAct: AccountByIdAct,
    private val eventBus: EventBus,
    private val transactionWriter: WriteTransactionDao,
) : ComposeViewModel<EditTransactionState, EditTransactionEvent>() {

    private val transactionType = mutableStateOf(TransactionType.EXPENSE)
    private val initialTitle = mutableStateOf<String?>(null)
    private val titleSuggestions = mutableStateOf(persistentSetOf<String>())
    private val currency = mutableStateOf("")
    private val description = mutableStateOf<String?>(null)
    private val dateTime = mutableStateOf<LocalDateTime?>(null)
    private val dueDate = mutableStateOf<LocalDateTime?>(null)
    private val date = MutableStateFlow<LocalDate?>(null)
    private val time = MutableStateFlow<LocalTime?>(null)
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val account = mutableStateOf<Account?>(null)
    private val toAccount = mutableStateOf<Account?>(null)
    private val category = mutableStateOf<Category?>(null)
    private val amount = mutableDoubleStateOf(0.0)
    private val hasChanges = mutableStateOf(false)
    private val displayLoanHelper = mutableStateOf(EditTransactionDisplayLoan())

    // This is used to when the transaction is associated with a loan/loan record,
    // used to indicate the background updating of loan/loanRecord data
    private val backgroundProcessingStarted = mutableStateOf(false)

    private val customExchangeRateState = mutableStateOf(CustomExchangeRateState())

    private var loadedTransaction: Transaction? = null
    private var editMode = false

    // Used for optimising in updating all loan/loanRecords
    private var accountsChanged = false

    private var title: String? = null
    private lateinit var baseUserCurrency: String

    fun start(screen: EditTransactionScreen) {
        viewModelScope.launch {
            editMode = screen.initialTransactionId != null

            baseUserCurrency = baseCurrency()

            val getAccounts = accountsAct(Unit)
            if (getAccounts.isEmpty()) {
                closeScreen()
                return@launch
            }
            accounts.value = getAccounts

            categories.value = categoriesAct(Unit)

            reset()

            loadedTransaction = screen.initialTransactionId?.let {
                trnByIdAct(it)
            } ?: Transaction(
                accountId = defaultAccountId(
                    screen = screen,
                    accounts = getAccounts
                ),
                categoryId = screen.categoryId,
                type = screen.type,
                amount = BigDecimal.ZERO,
                toAmount = BigDecimal.ZERO
            )

            display(loadedTransaction!!)
        }
    }

    @Composable
    override fun uiState(): EditTransactionState {
        return EditTransactionState(
            transactionType = getTransactionType(),
            initialTitle = getInitialTitle(),
            titleSuggestions = getTitleSuggestions(),
            currency = getCurrency(),
            description = getDescription(),
            dateTime = getDateTime(),
            dueDate = getDueDate(),
            accounts = getAccounts(),
            categories = getCategories(),
            account = getAccount(),
            toAccount = getToAccount(),
            category = getCategory(),
            amount = getAmount(),
            hasChanges = getHasChanges(),
            displayLoanHelper = getDisplayLoanHelper(),
            backgroundProcessingStarted = getBackgroundProcessingStarted(),
            customExchangeRateState = getCustomExchangeRateState()
        )
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType.value
    }

    @Composable
    private fun getInitialTitle(): String? {
        return initialTitle.value
    }

    @Composable
    private fun getTitleSuggestions(): ImmutableSet<String> {
        return titleSuggestions.value
    }

    @Composable
    private fun getCurrency(): String {
        return currency.value
    }

    @Composable
    private fun getDescription(): String? {
        return description.value
    }

    @Composable
    private fun getDateTime(): LocalDateTime? {
        return dateTime.value
    }

    @Composable
    private fun getDueDate(): LocalDateTime? {
        return dueDate.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccount(): Account? {
        return account.value
    }

    @Composable
    private fun getToAccount(): Account? {
        return toAccount.value
    }

    @Composable
    private fun getCategory(): Category? {
        return category.value
    }

    @Composable
    private fun getAmount(): Double {
        return amount.doubleValue
    }

    @Composable
    private fun getHasChanges(): Boolean {
        return hasChanges.value
    }

    @Composable
    private fun getDisplayLoanHelper(): EditTransactionDisplayLoan {
        return displayLoanHelper.value
    }

    @Composable
    private fun getBackgroundProcessingStarted(): Boolean {
        return backgroundProcessingStarted.value
    }

    @Composable
    private fun getCustomExchangeRateState(): CustomExchangeRateState {
        return customExchangeRateState.value
    }

    override fun onEvent(event: EditTransactionEvent) {
        when (event) {
            is EditTransactionEvent.CreateAccount -> createAccount(event.data)
            is EditTransactionEvent.CreateCategory -> createCategory(event.data)
            EditTransactionEvent.Delete -> delete()
            is EditTransactionEvent.EditCategory -> editCategory(event.updatedCategory)
            is EditTransactionEvent.OnAccountChanged -> onAccountChanged(event.newAccount)
            is EditTransactionEvent.OnAmountChanged -> onAmountChanged(event.newAmount)
            is EditTransactionEvent.OnCategoryChanged -> onCategoryChanged(event.newCategory)
            is EditTransactionEvent.OnDescriptionChanged ->
                onDescriptionChanged(event.newDescription)

            is EditTransactionEvent.OnDueDateChanged -> onDueDateChanged(event.newDueDate)
            EditTransactionEvent.OnPayPlannedPayment -> onPayPlannedPayment()
            is EditTransactionEvent.OnSetDateTime -> onSetDateTime(event.newDateTime)
            is EditTransactionEvent.OnSetDate -> onSetDate(event.newDate)
            is EditTransactionEvent.OnSetTime -> onSetTime(event.newTime)
            is EditTransactionEvent.OnSetTransactionType ->
                onSetTransactionType(event.newTransactionType)

            is EditTransactionEvent.OnTitleChanged -> onTitleChanged(event.newTitle)
            is EditTransactionEvent.OnToAccountChanged -> onToAccountChanged(event.newAccount)
            is EditTransactionEvent.Save -> save(event.closeScreen)
            is EditTransactionEvent.SetHasChanges -> setHasChanges(event.hasChangesValue)
            is EditTransactionEvent.UpdateExchangeRate -> updateExchangeRate(event.exRate)
        }
    }

    private suspend fun defaultAccountId(
        screen: EditTransactionScreen,
        accounts: List<Account>,
    ): UUID {
        if (screen.accountId != null) {
            return screen.accountId!!
        }

        val lastSelectedId = sharedPrefs.getString(
            SharedPrefs.LAST_SELECTED_ACCOUNT_ID,
            null
        )?.let { UUID.fromString(it) }
        if (lastSelectedId != null && ioThread {
                accounts.find {
                    it.id == lastSelectedId
                }
            } != null
        ) {
            // use last selected account
            return lastSelectedId
        }

        return accounts.first().id
    }

    private suspend fun display(transaction: Transaction) {
        this.title = transaction.title

        transactionType.value = transaction.type
        initialTitle.value = transaction.title
        dateTime.value = transaction.dateTime
        description.value = transaction.description
        dueDate.value = transaction.dueDate
        val selectedAccount = accountByIdAct(transaction.accountId)!!
        account.value = selectedAccount
        toAccount.value = transaction.toAccountId?.let {
            accountByIdAct(it)
        }
        category.value = transaction.categoryId?.let {
            categoryByIdAct(it)
        }
        amount.doubleValue = transaction.amount.toDouble()

        updateCurrency(account = selectedAccount)

        customExchangeRateState.value = if (transaction.toAccountId == null) {
            CustomExchangeRateState()
        } else {
            val exchangeRate = transaction.toAmount / transaction.amount
            val toAccountCurrency =
                accounts.value.find { acc -> acc.id == transaction.toAccountId }?.currency
            CustomExchangeRateState(
                showCard = toAccountCurrency != account.value?.currency,
                exchangeRate = exchangeRate.toDouble(),
                convertedAmount = transaction.toAmount.toDouble(),
                toCurrencyCode = toAccountCurrency,
                fromCurrencyCode = currency.value
            )
        }

        displayLoanHelper.value = getDisplayLoanHelper(trans = transaction)
    }

    private suspend fun getDisplayLoanHelper(trans: Transaction): EditTransactionDisplayLoan {
        if (trans.loanId == null) {
            return EditTransactionDisplayLoan()
        }

        val loan =
            ioThread { loanDao.findById(trans.loanId!!) } ?: return EditTransactionDisplayLoan()
        val isLoanRecord = trans.loanRecordId != null

        val loanWarningDescription = if (isLoanRecord) {
            "Note: This transaction is associated with a Loan Record of Loan : ${loan.name}\n" +
                    "You are trying to change the account associated with the loan record to an " +
                    "account of different currency" +
                    "\n The Loan Record will be re-calculated based on today's currency exchanges" +
                    " rates"
        } else {
            "Note: You are trying to change the account associated with the loan: ${loan.name} " +
                    "with an account of different currency, " +
                    "\nAll the loan records will be re-calculated based on today's currency " +
                    "exchanges rates "
        }

        val loanCaption =
            if (isLoanRecord) {
                "* This transaction is associated with a Loan Record of Loan : ${loan.name}"
            } else {
                "* This transaction is associated with Loan : ${loan.name}"
            }

        return EditTransactionDisplayLoan(
            isLoan = true,
            isLoanRecord = isLoanRecord,
            loanCaption = loanCaption,
            loanWarningDescription = loanWarningDescription
        )
    }

    private fun onAmountChanged(newAmount: Double) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                amount = newAmount.toBigDecimal()
            )
            amount.doubleValue = newAmount
            updateCustomExchangeRateState(amt = newAmount)

            saveIfEditMode()
        }
    }

    private fun onTitleChanged(newTitle: String?) {
        loadedTransaction = loadedTransaction().copy(
            title = newTitle
        )
        this.title = newTitle

        saveIfEditMode()

        updateTitleSuggestions(newTitle)
    }

    private fun onDescriptionChanged(newDescription: String?) {
        loadedTransaction = loadedTransaction().copy(
            description = newDescription
        )
        description.value = newDescription

        saveIfEditMode()
    }

    private fun onAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                accountId = newAccount.id
            )
            account.value = newAccount

            updateCustomExchangeRateState(fromAccount = newAccount)

            viewModelScope.launch {
                updateCurrency(account = newAccount)
            }

            accountsChanged = true

            // update last selected account
            sharedPrefs.putString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, newAccount.id.toString())

            saveIfEditMode()

            updateTitleSuggestions()
        }
    }

    private suspend fun updateCurrency(account: Account) {
        currency.value = account.currency ?: baseCurrency()
    }

    private fun onToAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                toAccountId = newAccount.id
            )
            toAccount.value = newAccount
            updateCustomExchangeRateState(toAccountValue = newAccount)

            saveIfEditMode()
        }
    }

    private fun onDueDateChanged(newDueDate: LocalDateTime?) {
        loadedTransaction = loadedTransaction().copy(
            dueDate = newDueDate
        )
        dueDate.value = newDueDate

        saveIfEditMode()
    }

    private fun onSetDateTime(newDateTime: LocalDateTime) {
        loadedTransaction = loadedTransaction().copy(
            dateTime = newDateTime
        )
        dateTime.value = newDateTime

        saveIfEditMode()
    }

    fun onSetDate(newDate: LocalDate) {
        loadedTransaction = loadedTransaction().copy(
            date = newDate
        )
        date.value = newDate
        onSetDateTime(
            getTrueDate(
                loadedTransaction?.date ?: dateNowLocal(),
                (dateTime.value?.toLocalTime() ?: timeUTC()),
                true
            )
        )
    }

    fun onSetTime(newTime: LocalTime) {
        loadedTransaction = loadedTransaction().copy(
            time = newTime
        )
        time.value = newTime
        onSetDateTime(
            getTrueDate(
                dateTime.value?.toLocalDate() ?: dateNowLocal(),
                loadedTransaction?.time ?: timeUTC(),
                true
            )
        )
    }

    private fun onSetTransactionType(newTransactionType: TransactionType) {
        loadedTransaction = loadedTransaction().copy(
            type = newTransactionType
        )
        transactionType.value = newTransactionType
        saveIfEditMode()
    }

    private fun onPayPlannedPayment() {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGet(
                transaction = loadedTransaction(),
                syncTransaction = false
            ) { paidTransaction ->
                loadedTransaction = paidTransaction
                dueDate.value = paidTransaction.dueDate
                dateTime.value = paidTransaction.dateTime

                saveIfEditMode(
                    closeScreen = true
                )
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            ioThread {
                loadedTransaction?.let {
                    transactionWriter.flagDeleted(it.id)
                }
                closeScreen()
            }
        }
    }

    private fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                categories.value = categoriesAct(Unit)

                // Select the newly created category
                onCategoryChanged(it)
            }
        }
    }

    private fun onCategoryChanged(newCategory: Category?) {
        loadedTransaction = loadedTransaction().copy(
            categoryId = newCategory?.id
        )
        category.value = newCategory

        saveIfEditMode()

        updateTitleSuggestions()
    }

    private fun updateTitleSuggestions(title: String? = loadedTransaction().title) {
        viewModelScope.launch {
            titleSuggestions.value = ioThread {
                smartTitleSuggestionsLogic.suggest(
                    title = title,
                    categoryId = category.value?.id,
                    accountId = account.value?.id
                )
            }.toPersistentSet()
        }
    }

    private fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                categories.value = categoriesAct(Unit)
            }
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                eventBus.post(AccountUpdatedEvent)
                accounts.value = accountsAct(Unit)
            }
        }
    }

    private fun save(closeScreen: Boolean = true) {
        if (!validateTransaction()) {
            return
        }

        viewModelScope.launch {
            saveInternal(closeScreen = closeScreen)
        }
    }

    private suspend fun saveInternal(closeScreen: Boolean) {
        try {
            ioThread {
                val amount = amount.doubleValue.toBigDecimal()

                loadedTransaction = loadedTransaction().copy(
                    accountId = account.value?.id ?: error("no accountId"),
                    toAccountId = toAccount.value?.id,
                    toAmount = customExchangeRateState.value.convertedAmount?.toBigDecimal()
                        ?: amount,
                    title = title?.trim(),
                    description = description.value?.trim(),
                    amount = amount,
                    type = transactionType.value,
                    dueDate = dueDate.value,
                    dateTime = when {
                        loadedTransaction().dateTime == null &&
                                dueDate.value == null -> {
                            timeNowUTC()
                        }

                        else -> loadedTransaction().dateTime
                    },
                    categoryId = category.value?.id,
                    isSynced = false
                )

                if (loadedTransaction?.loanId != null) {
                    loanTransactionsLogic.updateAssociatedLoanData(
                        loadedTransaction!!.copy(),
                        onBackgroundProcessingStart = {
                            backgroundProcessingStarted.value = true
                        },
                        onBackgroundProcessingEnd = {
                            backgroundProcessingStarted.value = false
                        },
                        accountsChanged = accountsChanged
                    )

                    // Reset Counter
                    accountsChanged = false
                }

                transactionWriter.save(loadedTransaction().toEntity())
                refreshWidget(WalletBalanceWidgetReceiver::class.java)
            }

            if (closeScreen) {
                closeScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHasChanges(hasChangesValue: Boolean) {
        hasChanges.value = hasChangesValue
    }

    private suspend fun transferToAmount(
        amount: Double
    ): Double? {
        if (transactionType.value != TransactionType.TRANSFER) return null
        val toCurrency = toAccount.value?.currency ?: baseCurrency()
        val fromCurrency = account.value?.currency ?: baseCurrency()

        return exchangeRatesLogic.convertAmount(
            baseCurrency = baseCurrency(),
            amount = amount,
            fromCurrency = fromCurrency,
            toCurrency = toCurrency
        )
    }

    private suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }

    private fun closeScreen() {
        if (nav.backStackEmpty()) {
            nav.resetBackStack()
            nav.navigateTo(MainScreen)
        } else {
            nav.back()
        }
    }

    private fun validateTransaction(): Boolean {
        if (transactionType.value == TransactionType.TRANSFER && toAccount.value == null) {
            return false
        }

        if (amount.doubleValue == 0.0) {
            return false
        }

        return true
    }

    private fun reset() {
        loadedTransaction = null

        initialTitle.value = null
        description.value = null
        dueDate.value = null
        category.value = null
        hasChanges.value = false
    }

    private fun loadedTransaction() = loadedTransaction ?: error("Loaded transaction is null")

    private fun updateExchangeRate(exRate: Double?) {
        viewModelScope.launch {
            updateCustomExchangeRateState(exchangeRate = exRate, resetRate = exRate == null)
        }
    }

    private suspend fun updateCustomExchangeRateState(
        toAccountValue: Account? = null,
        fromAccount: Account? = null,
        amt: Double? = null,
        exchangeRate: Double? = null,
        resetRate: Boolean = false
    ) {
        computationThread {
            val toAcc = toAccountValue ?: toAccount.value
            val fromAcc = fromAccount ?: account.value

            val toAccCurrencyCode = toAcc?.currency ?: baseUserCurrency
            val fromAccCurrencyCode = fromAcc?.currency ?: baseUserCurrency

            if (toAcc == null || fromAcc == null || (toAccCurrencyCode == fromAccCurrencyCode)) {
                customExchangeRateState.value = CustomExchangeRateState()
                return@computationThread
            }

            val exRate = exchangeRate
                ?: if (customExchangeRateState.value.showCard &&
                    toAccCurrencyCode == customExchangeRateState.value.toCurrencyCode &&
                    fromAccCurrencyCode == customExchangeRateState.value.fromCurrencyCode &&
                    !resetRate
                ) {
                    customExchangeRateState.value.exchangeRate
                } else {
                    exchangeRatesLogic.convertAmount(
                        baseCurrency = baseUserCurrency,
                        amount = 1.0,
                        fromCurrency = fromAccCurrencyCode,
                        toCurrency = toAccCurrencyCode
                    )
                }

            val amount = amt ?: amount.doubleValue

            val customTransferExchangeRateState = CustomExchangeRateState(
                showCard = true,
                toCurrencyCode = toAccCurrencyCode,
                fromCurrencyCode = fromAccCurrencyCode,
                exchangeRate = exRate,
                convertedAmount = exRate * amount
            )

            customExchangeRateState.value = customTransferExchangeRateState
            uiThread {
                saveIfEditMode()
            }
        }
    }

    private fun saveIfEditMode(closeScreen: Boolean = false) {
        if (editMode) {
            hasChanges.value = true

            save(closeScreen)
        }
    }
}