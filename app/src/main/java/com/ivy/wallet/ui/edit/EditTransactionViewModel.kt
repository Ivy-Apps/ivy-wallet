package com.ivy.wallet.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.domain.action.account.AccountByIdAct
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.category.CategoryByIdAct
import com.ivy.wallet.domain.action.transaction.TrnByIdAct
import com.ivy.wallet.domain.data.CustomExchangeRateState
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.*
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.uploader.TransactionUploader
import com.ivy.wallet.domain.event.AccountsUpdatedEvent
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.refreshWidget
import com.ivy.wallet.ui.EditTransaction
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.loan.data.EditTransactionDisplayLoan
import com.ivy.wallet.ui.widget.WalletBalanceReceiver
import com.ivy.wallet.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val loanDao: LoanDao,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,
    private val transactionUploader: TransactionUploader,
    private val sharedPrefs: SharedPrefs,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val paywallLogic: PaywallLogic,
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val smartTitleSuggestionsLogic: SmartTitleSuggestionsLogic,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val trnByIdAct: TrnByIdAct,
    private val categoryByIdAct: CategoryByIdAct,
    private val accountByIdAct: AccountByIdAct
) : ViewModel() {

    private val _transactionType = MutableLiveData<TransactionType>()
    val transactionType = _transactionType

    private val _initialTitle = MutableStateFlow<String?>(null)
    val initialTitle = _initialTitle.readOnly()

    private val _titleSuggestions = MutableStateFlow(emptySet<String>())
    val titleSuggestions = _titleSuggestions.asStateFlow()

    private val _currency = MutableStateFlow("")
    val currency = _currency.readOnly()

    private val _description = MutableStateFlow<String?>(null)
    val description = _description.readOnly()

    private val _dateTime = MutableStateFlow<LocalDateTime?>(null)
    val dateTime = _dateTime.readOnly()

    private val _dueDate = MutableStateFlow<LocalDateTime?>(null)
    val dueDate = _dueDate.readOnly()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.readOnly()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.readOnly()

    private val _account = MutableStateFlow<Account?>(null)
    val account = _account.readOnly()

    private val _toAccount = MutableStateFlow<Account?>(null)
    val toAccount = _toAccount.readOnly()

    private val _category = MutableStateFlow<Category?>(null)
    val category = _category.readOnly()

    private val _amount = MutableStateFlow(0.0)
    val amount = _amount.readOnly()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges = _hasChanges.readOnly()

    private val _displayLoanHelper: MutableStateFlow<EditTransactionDisplayLoan> =
        MutableStateFlow(EditTransactionDisplayLoan())
    val displayLoanHelper = _displayLoanHelper.asStateFlow()

    //This is used to when the transaction is associated with a loan/loan record,
    // used to indicate the background updating of loan/loanRecord data
    private val _backgroundProcessingStarted = MutableStateFlow(false)
    val backgroundProcessingStarted = _backgroundProcessingStarted.asStateFlow()

    private val _customExchangeRateState = MutableStateFlow(CustomExchangeRateState())
    val customExchangeRateState = _customExchangeRateState.asStateFlow()

    private var loadedTransaction: Transaction? = null
    private var editMode = false

    //Used for optimising in updating all loan/loanRecords
    private var accountsChanged = false

    var title: String? = null
    private lateinit var baseUserCurrency: String

    fun start(screen: EditTransaction) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            editMode = screen.initialTransactionId != null

            baseUserCurrency = baseCurrency()

            val accounts = accountsAct(Unit)
            if (accounts.isEmpty()) {
                closeScreen()
                return@launch
            }
            _accounts.value = accounts

            _categories.value = categoriesAct(Unit)

            reset()

            loadedTransaction = screen.initialTransactionId?.let {
                trnByIdAct(it)
            } ?: Transaction(
                accountId = defaultAccountId(
                    screen = screen,
                    accounts = accounts
                ),
                categoryId = screen.categoryId,
                type = screen.type,
                amount = BigDecimal.ZERO,
                toAmount = BigDecimal.ZERO
            )

            display(loadedTransaction!!)

            TestIdlingResource.decrement()
        }
    }

    private suspend fun getDisplayLoanHelper(trans: Transaction): EditTransactionDisplayLoan {
        if (trans.loanId == null)
            return EditTransactionDisplayLoan()

        val loan =
            ioThread { loanDao.findById(trans.loanId) } ?: return EditTransactionDisplayLoan()
        val isLoanRecord = trans.loanRecordId != null

        val loanWarningDescription = if (isLoanRecord)
            "Note: This transaction is associated with a Loan Record of Loan : ${loan.name}\n" +
                    "You are trying to change the account associated with the loan record to an account of different currency" +
                    "\n The Loan Record will be re-calculated based on today's currency exchanges rates"
        else {
            "Note: You are trying to change the account associated with the loan: ${loan.name} with an account " +
                    "of different currency, " +
                    "\nAll the loan records will be re-calculated based on today's currency exchanges rates "
        }

        val loanCaption =
            if (isLoanRecord) "* This transaction is associated with a Loan Record of Loan : ${loan.name}"
            else "* This transaction is associated with Loan : ${loan.name}"

        return EditTransactionDisplayLoan(
            isLoan = true,
            isLoanRecord = isLoanRecord,
            loanCaption = loanCaption,
            loanWarningDescription = loanWarningDescription
        )
    }

    private suspend fun defaultAccountId(
        screen: EditTransaction,
        accounts: List<Account>,
    ): UUID {
        if (screen.accountId != null) {
            return screen.accountId
        }

        val lastSelectedId = sharedPrefs.getString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, null)
            ?.let { UUID.fromString(it) }
        if (lastSelectedId != null && ioThread { accounts.find { it.id == lastSelectedId } } != null) {
            //use last selected account
            return lastSelectedId
        }

        return accounts.first().id
    }

    private suspend fun display(transaction: Transaction) {
        this.title = transaction.title

        _transactionType.value = transaction.type
        _initialTitle.value = transaction.title
        _dateTime.value = transaction.dateTime
        _description.value = transaction.description
        _dueDate.value = transaction.dueDate
        val selectedAccount = accountByIdAct(transaction.accountId)!!
        _account.value = selectedAccount
        _toAccount.value = transaction.toAccountId?.let {
            accountByIdAct(it)
        }
        _category.value = transaction.categoryId?.let {
            categoryByIdAct(it)
        }
        _amount.value = transaction.amount.toDouble()

        updateCurrency(account = selectedAccount)

        _customExchangeRateState.value = if (transaction.toAccountId == null)
            CustomExchangeRateState()
        else {
            val exchangeRate = transaction.toAmount / transaction.amount
            val toAccountCurrency =
                _accounts.value.find { acc -> acc.id == transaction.toAccountId }?.currency
            CustomExchangeRateState(
                showCard = toAccountCurrency != account.value?.currency,
                exchangeRate = exchangeRate.toDouble(),
                convertedAmount = transaction.toAmount.toDouble(),
                toCurrencyCode = toAccountCurrency,
                fromCurrencyCode = currency.value
            )
        }

        _displayLoanHelper.value = getDisplayLoanHelper(trans = transaction)
    }

    private suspend fun updateCurrency(account: Account) {
        _currency.value = account.currency ?: baseCurrency()
    }

    private suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }

    fun onAmountChanged(newAmount: Double) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                amount = newAmount.toBigDecimal()
            )
            _amount.value = newAmount
            updateCustomExchangeRateState(amt = newAmount)

            saveIfEditMode()
        }
    }

    fun onTitleChanged(newTitle: String?) {
        loadedTransaction = loadedTransaction().copy(
            title = newTitle
        )
        this.title = newTitle

        saveIfEditMode()

        updateTitleSuggestions(newTitle)
    }

    private fun updateTitleSuggestions(title: String? = loadedTransaction().title) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _titleSuggestions.value = ioThread {
                smartTitleSuggestionsLogic.suggest(
                    title = title,
                    categoryId = category.value?.id,
                    accountId = account.value?.id
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun onDescriptionChanged(newDescription: String?) {
        loadedTransaction = loadedTransaction().copy(
            description = newDescription
        )
        _description.value = newDescription

        saveIfEditMode()
    }

    fun onCategoryChanged(newCategory: Category?) {
        loadedTransaction = loadedTransaction().copy(
            categoryId = newCategory?.id
        )
        _category.value = newCategory

        saveIfEditMode()

        updateTitleSuggestions()
    }

    fun onAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loadedTransaction = loadedTransaction().copy(
                accountId = newAccount.id
            )
            _account.value = newAccount

            updateCustomExchangeRateState(fromAccount = newAccount)

            viewModelScope.launch {
                updateCurrency(account = newAccount)
            }

            accountsChanged = true

            //update last selected account
            sharedPrefs.putString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, newAccount.id.toString())

            saveIfEditMode()

            updateTitleSuggestions()

            TestIdlingResource.decrement()
        }
    }

    fun onToAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                toAccountId = newAccount.id
            )
            _toAccount.value = newAccount
            updateCustomExchangeRateState(toAccount = newAccount)

            saveIfEditMode()
        }
    }

    fun onDueDateChanged(newDueDate: LocalDateTime?) {
        loadedTransaction = loadedTransaction().copy(
            dueDate = newDueDate
        )
        _dueDate.value = newDueDate

        saveIfEditMode()
    }

    fun onSetDateTime(newDateTime: LocalDateTime) {
        loadedTransaction = loadedTransaction().copy(
            dateTime = newDateTime
        )
        _dateTime.value = newDateTime

        saveIfEditMode()
    }

    fun onSetTransactionType(newTransactionType: TransactionType) {
        loadedTransaction = loadedTransaction().copy(
            type = newTransactionType
        )
        _transactionType.value = newTransactionType

        saveIfEditMode()
    }


    fun onPayPlannedPayment() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            plannedPaymentsLogic.payOrGet(
                transaction = loadedTransaction(),
                syncTransaction = false
            ) { paidTransaction ->
                loadedTransaction = paidTransaction
                _dueDate.value = paidTransaction.dueDate
                _dateTime.value = paidTransaction.dateTime

                saveIfEditMode(
                    closeScreen = true
                )
            }

            TestIdlingResource.decrement()
        }
    }


    fun delete() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                loadedTransaction?.let {
                    transactionDao.flagDeleted(it.id)
                }
                closeScreen()

                loadedTransaction?.let {
                    transactionUploader.delete(it.id)
                }
            }

            TestIdlingResource.decrement()
        }
    }

    fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            categoryCreator.createCategory(data) {
                _categories.value = categoriesAct(Unit)

                //Select the newly created category
                onCategoryChanged(it)
            }

            TestIdlingResource.decrement()
        }
    }

    fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            categoryCreator.editCategory(updatedCategory) {
                _categories.value = categoriesAct(Unit)
            }

            TestIdlingResource.decrement()
        }
    }

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                EventBus.getDefault().post(AccountsUpdatedEvent())
                _accounts.value = accountsAct(Unit)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun saveIfEditMode(closeScreen: Boolean = false) {
        if (editMode) {
            _hasChanges.value = true

            save(closeScreen)
        }
    }

    fun save(closeScreen: Boolean = true) {
        if (!validateTransaction()) {
            return
        }

        viewModelScope.launch {
            TestIdlingResource.increment()

            paywallLogic.protectQuotaExceededWithPaywall(
                onPaywallHit = {
                    nav.back()
                }
            ) {
                saveInternal(closeScreen = closeScreen)
            }

            TestIdlingResource.decrement()
        }
    }

    private suspend fun saveInternal(closeScreen: Boolean) {
        try {
            ioThread {
                val amount = amount.value.toBigDecimal()

                loadedTransaction = loadedTransaction().copy(
                    accountId = account.value?.id ?: error("no accountId"),
                    toAccountId = toAccount.value?.id,
                    toAmount = _customExchangeRateState.value.convertedAmount?.toBigDecimal()
                        ?: amount,
                    title = title?.trim(),
                    description = description.value?.trim(),
                    amount = amount,
                    type = transactionType.value ?: error("no transaction type"),
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
                            _backgroundProcessingStarted.value = true
                        },
                        onBackgroundProcessingEnd = {
                            _backgroundProcessingStarted.value = false
                        },
                        accountsChanged = accountsChanged
                    )

                    //Reset Counter
                    accountsChanged = false
                }

                transactionDao.save(loadedTransaction().toEntity())
                refreshWidget(WalletBalanceReceiver::class.java)
            }

            if (closeScreen) {
                closeScreen()

                ioThread {
                    transactionUploader.sync(loadedTransaction())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setHasChanges(hasChanges: Boolean) {
        _hasChanges.value = hasChanges
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

    private fun closeScreen() {
        if (nav.backStackEmpty()) {
            nav.resetBackStack()
            nav.navigateTo(Main)
        } else {
            nav.back()
        }
    }

    private fun validateTransaction(): Boolean {
        if (transactionType.value == TransactionType.TRANSFER && toAccount.value == null) {
            return false
        }

        if (amount.value == 0.0) {
            return false
        }

        return true
    }

    private fun reset() {
        loadedTransaction = null

        _initialTitle.value = null
        _description.value = null
        _dueDate.value = null
        _category.value = null
        _hasChanges.value = false
    }

    private fun loadedTransaction() = loadedTransaction ?: error("Loaded transaction is null")

    private suspend fun updateCustomExchangeRateState(
        toAccount: Account? = null,
        fromAccount: Account? = null,
        amt: Double? = null,
        exchangeRate: Double? = null,
        resetRate: Boolean = false
    ) {
        computationThread {
            val toAcc = toAccount ?: _toAccount.value
            val fromAcc = fromAccount ?: _account.value

            val toAccCurrencyCode = toAcc?.currency ?: baseUserCurrency
            val fromAccCurrencyCode = fromAcc?.currency ?: baseUserCurrency

            if (toAcc == null || fromAcc == null || (toAccCurrencyCode == fromAccCurrencyCode)) {
                _customExchangeRateState.value = CustomExchangeRateState()
                return@computationThread
            }

            val exRate = exchangeRate
                ?: if (customExchangeRateState.value.showCard && toAccCurrencyCode == customExchangeRateState.value.toCurrencyCode
                    && fromAccCurrencyCode == customExchangeRateState.value.fromCurrencyCode && !resetRate
                )
                    customExchangeRateState.value.exchangeRate
                else
                    exchangeRatesLogic.convertAmount(
                        baseCurrency = baseUserCurrency,
                        amount = 1.0,
                        fromCurrency = fromAccCurrencyCode,
                        toCurrency = toAccCurrencyCode
                    )


            val amount = amt ?: _amount.value ?: 0.0

            val customTransferExchangeRateState = CustomExchangeRateState(
                showCard = true,
                toCurrencyCode = toAccCurrencyCode,
                fromCurrencyCode = fromAccCurrencyCode,
                exchangeRate = exRate,
                convertedAmount = exRate * amount
            )

            _customExchangeRateState.value = customTransferExchangeRateState
            uiThread {
                saveIfEditMode()
            }
        }
    }

    fun updateExchangeRate(exRate: Double?) {
        viewModelScope.launch {
            updateCustomExchangeRateState(exchangeRate = exRate, resetRate = exRate == null)
        }
    }
}