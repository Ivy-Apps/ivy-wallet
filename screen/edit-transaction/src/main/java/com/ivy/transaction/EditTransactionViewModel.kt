package com.ivy.transaction

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.ivy.base.Toaster
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.refreshWidget
import com.ivy.base.model.TransactionType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Tag
import com.ivy.data.model.TagId
import com.ivy.data.model.TransactionId
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.TagRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TagMapper
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.domain.features.Features
import com.ivy.legacy.data.EditTransactionDisplayLoan
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.computationThread
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.toLowerCaseLocal
import com.ivy.legacy.utils.uiThread
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.R
import com.ivy.ui.time.impl.DateTimePicker
import com.ivy.wallet.domain.action.account.AccountByIdAct
import com.ivy.wallet.domain.action.account.AccountsAct
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@Suppress("LargeClass")
@Stable
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val toaster: Toaster,
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
    private val categoryRepository: CategoryRepository,
    private val trnByIdAct: TrnByIdAct,
    private val accountByIdAct: AccountByIdAct,
    private val transactionRepo: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val tagRepository: TagRepository,
    private val tagMapper: TagMapper,
    private val features: Features,
    private val timeConverter: TimeConverter,
    private val timeProvider: TimeProvider,
    private val dateTimePicker: DateTimePicker,
) : ComposeViewModel<EditTransactionViewState, EditTransactionViewEvent>() {

    private var transactionType by mutableStateOf(TransactionType.EXPENSE)
    private var initialTitle by mutableStateOf<String?>(null)
    private var titleSuggestions by mutableStateOf(persistentSetOf<String>())
    private var currency by mutableStateOf("")
    private var description by mutableStateOf<String?>(null)
    private var dateTime by mutableStateOf<Instant?>(null)
    private var dueDate by mutableStateOf<Instant?>(null)
    private var accounts by mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private var categories by mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private var tags by mutableStateOf<ImmutableList<Tag>>(persistentListOf())
    private var transactionAssociatedTags by mutableStateOf<ImmutableList<TagId>>(persistentListOf())
    private var account by mutableStateOf<Account?>(null)
    private var toAccount by mutableStateOf<Account?>(null)
    private var category by mutableStateOf<Category?>(null)
    private var amount by mutableDoubleStateOf(0.0)
    private var hasChanges by mutableStateOf(false)
    private var displayLoanHelper by mutableStateOf(EditTransactionDisplayLoan())

    private var paidHistory: Instant? = null

    // This is used to when the transaction is associated with a loan/loan record,
    // used to indicate the background updating of loan/loanRecord data
    private var backgroundProcessingStarted by mutableStateOf(false)

    private var customExchangeRateState by mutableStateOf(CustomExchangeRateState())

    private var loadedTransaction: Transaction? = null
    private var editMode = false

    // Used for optimising in updating all loan/loanRecords
    private var accountsChanged = false

    private var title: String? = null
    private lateinit var baseUserCurrency: String
    private var tagSearchJob: Job? = null
    private val tagSearchDebounceTimeInMills: Long = 500

    fun start(screen: EditTransactionScreen) {
        viewModelScope.launch {
            editMode = screen.initialTransactionId != null

            baseUserCurrency = baseCurrency()

            val tagList = async { getAllTags() }

            val getAccounts = accountsAct(Unit)
            if (getAccounts.isEmpty()) {
                closeScreen()
                return@launch
            }
            accounts = getAccounts

            categories = sortCategories()

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

            tags = tagList.await()
            transactionAssociatedTags =
                tagRepository.findByAssociatedId(AssociationId(loadedTransaction().id)).map(Tag::id)
                    .toImmutableList()
            display(loadedTransaction!!)
        }
    }

    @Composable
    override fun uiState(): EditTransactionViewState {
        return EditTransactionViewState(
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
            customExchangeRateState = getCustomExchangeRateState(),
            tags = getTags(),
            transactionAssociatedTags = getTransactionAssociatedTags()
        )
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType
    }

    @Composable
    private fun getInitialTitle(): String? {
        return initialTitle
    }

    @Composable
    private fun getTitleSuggestions(): ImmutableSet<String> {
        return if (features.showTitleSuggestions.asEnabledState()) {
            titleSuggestions
        } else {
            persistentSetOf()
        }
    }

    @Composable
    private fun getCurrency(): String {
        return currency
    }

    @Composable
    private fun getDescription(): String? {
        return description
    }

    @Composable
    private fun getDateTime(): Instant? {
        return dateTime
    }

    @Composable
    private fun getDueDate(): Instant? {
        return dueDate
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories
    }

    @Composable
    private fun getAccount(): Account? {
        return account
    }

    @Composable
    private fun getToAccount(): Account? {
        return toAccount
    }

    @Composable
    private fun getCategory(): Category? {
        return category
    }

    @Composable
    private fun getAmount(): Double {
        return amount
    }

    @Composable
    private fun getHasChanges(): Boolean {
        return hasChanges
    }

    @Composable
    private fun getDisplayLoanHelper(): EditTransactionDisplayLoan {
        return displayLoanHelper
    }

    @Composable
    private fun getBackgroundProcessingStarted(): Boolean {
        return backgroundProcessingStarted
    }

    @Composable
    private fun getCustomExchangeRateState(): CustomExchangeRateState {
        return customExchangeRateState
    }

    @Composable
    private fun getTags(): ImmutableList<Tag> {
        return tags
    }

    @Composable
    private fun getTransactionAssociatedTags(): ImmutableList<TagId> {
        return transactionAssociatedTags
    }

    @Suppress("CyclomaticComplexMethod")
    override fun onEvent(event: EditTransactionViewEvent) {
        when (event) {
            is EditTransactionViewEvent.CreateAccount -> createAccount(event.data)
            is EditTransactionViewEvent.CreateCategory -> createCategory(event.data)
            EditTransactionViewEvent.Delete -> delete()
            EditTransactionViewEvent.Duplicate -> duplicate()
            is EditTransactionViewEvent.EditCategory -> editCategory(event.updatedCategory)
            is EditTransactionViewEvent.OnAccountChanged -> onAccountChanged(event.newAccount)
            is EditTransactionViewEvent.OnAmountChanged -> onAmountChanged(event.newAmount)
            is EditTransactionViewEvent.OnCategoryChanged -> onCategoryChanged(event.newCategory)
            is EditTransactionViewEvent.OnDescriptionChanged ->
                onDescriptionChanged(event.newDescription)

            is EditTransactionViewEvent.OnDueDateChanged -> onDueDateChanged(event.newDueDate)
            EditTransactionViewEvent.OnPayPlannedPayment -> onPayPlannedPayment()
            is EditTransactionViewEvent.OnChangeDate -> handleChangeDate()
            is EditTransactionViewEvent.OnChangeTime -> handleChangeTime()
            is EditTransactionViewEvent.OnSetTransactionType ->
                onSetTransactionType(event.newTransactionType)

            is EditTransactionViewEvent.OnTitleChanged -> onTitleChanged(event.newTitle)
            is EditTransactionViewEvent.OnToAccountChanged -> onToAccountChanged(event.newAccount)
            is EditTransactionViewEvent.Save -> save(event.closeScreen)
            is EditTransactionViewEvent.SetHasChanges -> setHasChanges(event.hasChangesValue)
            is EditTransactionViewEvent.UpdateExchangeRate -> updateExchangeRate(event.exRate)
            is EditTransactionViewEvent.TagEvent -> handleTagEvent(event)
        }
    }

    private fun handleTagEvent(event: EditTransactionViewEvent.TagEvent) {
        when (event) {
            is EditTransactionViewEvent.TagEvent.SaveTag -> onTagSaved(event.name)
            is EditTransactionViewEvent.TagEvent.OnTagSelect -> associateTagToTransaction(event.selectedTag)
            is EditTransactionViewEvent.TagEvent.OnTagDeSelect -> removeTagAssociation(event.selectedTag)
            is EditTransactionViewEvent.TagEvent.OnTagSearch -> searchTag(event.query)
            is EditTransactionViewEvent.TagEvent.OnTagDelete -> deleteTag(event.selectedTag)
            is EditTransactionViewEvent.TagEvent.OnTagEdit -> updateTagInformation(event.newTag)
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

        transactionType = transaction.type
        initialTitle = transaction.title
        dateTime = transaction.dateTime
        description = transaction.description
        dueDate = transaction.dueDate
        paidHistory = transaction.paidFor
        val selectedAccount = accountByIdAct(transaction.accountId)!!
        account = selectedAccount
        toAccount = transaction.toAccountId?.let {
            accountByIdAct(it)
        }
        category = transaction.categoryId?.let {
            categoryRepository.findById(CategoryId(it))
        }
        amount = transaction.amount.toDouble()

        updateCurrency(account = selectedAccount)

        customExchangeRateState = if (transaction.toAccountId == null) {
            CustomExchangeRateState()
        } else {
            val exchangeRate = transaction.toAmount / transaction.amount
            val toAccountCurrency =
                accounts.find { acc -> acc.id == transaction.toAccountId }?.currency
            CustomExchangeRateState(
                showCard = toAccountCurrency != account?.currency,
                exchangeRate = exchangeRate.toDouble(),
                convertedAmount = transaction.toAmount.toDouble(),
                toCurrencyCode = toAccountCurrency,
                fromCurrencyCode = currency
            )
        }

        displayLoanHelper = getDisplayLoanHelper(trans = transaction)
    }

    private suspend fun getDisplayLoanHelper(trans: Transaction): EditTransactionDisplayLoan {
        if (trans.loanId == null) {
            return EditTransactionDisplayLoan()
        }

        val loan =
            ioThread { loanDao.findById(trans.loanId!!) } ?: return EditTransactionDisplayLoan()
        val isLoanRecord = trans.loanRecordId != null

        val loanWarningDescription = if (isLoanRecord) {
            context.getString(
                R.string.note_transaction_associated_with_loan_record_of_loan,
                loan.name
            )
        } else {
            context.getString(
                R.string.note_you_are_trying_to_change_the_account_associated_with_the_loan,
                loan.name
            )
        }

        val loanCaption =
            if (isLoanRecord) {
                context.getString(
                    R.string.this_transaction_is_associated_with_loan_record,
                    loan.name
                )
            } else {
                context.getString(
                    R.string.this_transaction_is_associated_with_loan,
                    loan.name
                )
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
            amount = newAmount
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
        description = newDescription

        saveIfEditMode()
    }

    private fun onAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                accountId = newAccount.id
            )
            account = newAccount

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
        currency = account.currency ?: baseCurrency()
    }

    private fun onToAccountChanged(newAccount: Account) {
        viewModelScope.launch {
            loadedTransaction = loadedTransaction().copy(
                toAccountId = newAccount.id
            )
            toAccount = newAccount
            updateCustomExchangeRateState(toAccountValue = newAccount)

            saveIfEditMode()
        }
    }

    private fun onDueDateChanged(newDueDate: LocalDateTime?) {
        val newDueDateUtc = with(timeConverter) { newDueDate?.toUTC() }
        loadedTransaction = loadedTransaction().copy(
            dueDate = newDueDateUtc
        )
        dueDate = newDueDateUtc

        saveIfEditMode()
    }

    private fun handleChangeDate() {
        dateTimePicker.pickDate(
            initialDate = loadedTransaction?.dateTime,
        ) { localDate ->
            val localTime = loadedTransaction().dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()
            loadedTransaction = loadedTransaction().copy(
                date = localDate,
            )
            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun handleChangeTime() {
        dateTimePicker.pickTime(
            initialTime = loadedTransaction?.dateTime?.let {
                with(timeConverter) {
                    it.toLocalDateTime()
                }
            }?.toLocalTime()
        ) { localTime ->
            val localDate = loadedTransaction().dateTime?.let {
                with(timeConverter) { it.toLocalDate() }
            } ?: timeProvider.localDateNow()
            loadedTransaction = loadedTransaction().copy(
                time = localTime,
            )
            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun updateDateTime(newDateTime: LocalDateTime) {
        val newDateTimeUtc = with(timeConverter) { newDateTime.toUTC() }
        loadedTransaction = loadedTransaction().copy(
            dateTime = newDateTimeUtc,
        )
        dateTime = newDateTimeUtc

        saveIfEditMode()
    }

    private fun onSetTransactionType(newTransactionType: TransactionType) {
        loadedTransaction = loadedTransaction().copy(
            type = newTransactionType
        )
        transactionType = newTransactionType
        saveIfEditMode()
    }

    private fun onPayPlannedPayment() {
        viewModelScope.launch {
            plannedPaymentsLogic.payOrGetLegacy(
                transaction = loadedTransaction(),
                syncTransaction = false
            ) { paidTransaction ->
                loadedTransaction = paidTransaction
                paidHistory = paidTransaction.paidFor
                dueDate = paidTransaction.dueDate
                dateTime = paidTransaction.dateTime

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
                    transactionRepo.deleteById(TransactionId(it.id))
                }
                closeScreen()
            }
        }
    }

    private fun duplicate() {
        viewModelScope.launch {
            ioThread {
                val id = UUID.randomUUID()
                 loadedTransaction()
                    .copy(
                        id = id,
                        dateTime = timeProvider.utcNow(),
                    )
                    .toDomain(transactionMapper)
                    ?.let {
                        transactionRepo.save(it)
                    }

                tagRepository.findByIds(transactionAssociatedTags).forEach {
                    associateTagToTransaction(it, id)
                }

                refreshWidget(WalletBalanceWidgetReceiver::class.java)
            }

            closeScreen()
        }
    }

    private fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                categories = sortCategories()

                // Select the newly created category
                onCategoryChanged(it)
            }
        }
    }

    private fun onCategoryChanged(newCategory: Category?) {
        loadedTransaction = loadedTransaction().copy(
            categoryId = newCategory?.id?.value
        )
        category = newCategory

        saveIfEditMode()

        updateTitleSuggestions()
    }

    private fun updateTitleSuggestions(title: String? = loadedTransaction().title) {
        viewModelScope.launch {
            titleSuggestions = ioThread {
                smartTitleSuggestionsLogic.suggest(
                    title = title,
                    categoryId = category?.id?.value,
                    accountId = account?.id
                )
            }.toPersistentSet()
        }
    }

    private fun editCategory(updatedCategory: Category) {
        viewModelScope.launch {
            categoryCreator.editCategory(updatedCategory) {
                categories = sortCategories()
            }
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                accounts = accountsAct(Unit)
            }
        }
    }

    private fun save(closeScreen: Boolean = true) {
        if (!validTransaction()) {
            return
        }

        viewModelScope.launch {
            saveInternal(closeScreen = closeScreen)
        }
    }

    private suspend fun saveInternal(closeScreen: Boolean) {
        try {
            ioThread {
                val amount = amount.toBigDecimal()

                loadedTransaction = loadedTransaction().copy(
                    accountId = account?.id ?: error("no accountId"),
                    toAccountId = toAccount?.id,
                    toAmount = customExchangeRateState.convertedAmount?.toBigDecimal()
                        ?: amount,
                    title = title?.trim(),
                    description = description?.trim(),
                    amount = amount,
                    type = transactionType,
                    dueDate = dueDate,
                    paidFor = paidHistory,
                    dateTime = when {
                        loadedTransaction().dateTime == null &&
                                dueDate == null -> {
                            timeProvider.utcNow()
                        }

                        else -> loadedTransaction().dateTime
                    },
                    categoryId = category?.id?.value,
                    isSynced = false
                )

                if (loadedTransaction?.loanId != null) {
                    loanTransactionsLogic.updateAssociatedLoanData(
                        loadedTransaction!!.copy(),
                        onBackgroundProcessingStart = {
                            backgroundProcessingStarted = true
                        },
                        onBackgroundProcessingEnd = {
                            backgroundProcessingStarted = false
                        },
                        accountsChanged = accountsChanged
                    )

                    // Reset Counter
                    accountsChanged = false
                }

                loadedTransaction().toDomain(transactionMapper)?.let {
                    transactionRepo.save(it)
                }

                refreshWidget(WalletBalanceWidgetReceiver::class.java)
            }

            if (closeScreen) {
                closeScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmName("setHasChangesMethod")
    private fun setHasChanges(hasChangesValue: Boolean) {
        hasChanges = hasChangesValue
    }

    private suspend fun transferToAmount(
        amount: Double
    ): Double? {
        if (transactionType != TransactionType.TRANSFER) return null
        val toCurrency = toAccount?.currency ?: baseCurrency()
        val fromCurrency = account?.currency ?: baseCurrency()

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

    // Comment for push
    @Suppress("ReturnCount")
    private fun validTransaction(): Boolean {
        if (hasChosenSameSourceAndDestinationAccountToTransfer()) {
            viewModelScope.launch {
                toaster.show(R.string.msg_source_account_destination_account_same_for_transfer)
            }
            return false
        }
        if (hasNotChosenAccountToTransfer()) {
            viewModelScope.launch {
                toaster.show(R.string.msg_select_account_to_transfer)
            }
            return false
        }

        if (amount == 0.0) {
            return false
        }

        return true
    }

    private fun hasNotChosenAccountToTransfer(): Boolean {
        return transactionType == TransactionType.TRANSFER && toAccount == null
    }

    private fun hasChosenSameSourceAndDestinationAccountToTransfer(): Boolean {
        return transactionType == TransactionType.TRANSFER && toAccount == account
    }

    private fun reset() {
        loadedTransaction = null

        initialTitle = null
        description = null
        dueDate = null
        category = null
        hasChanges = false
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
            val toAcc = toAccountValue ?: toAccount
            val fromAcc = fromAccount ?: account

            val toAccCurrencyCode = toAcc?.currency ?: baseUserCurrency
            val fromAccCurrencyCode = fromAcc?.currency ?: baseUserCurrency

            if (toAcc == null || fromAcc == null || (toAccCurrencyCode == fromAccCurrencyCode)) {
                customExchangeRateState = CustomExchangeRateState()
                return@computationThread
            }

            val exRate = exchangeRate
                ?: if (isCustomExchangeRateCurrencyCodeMatchingWithSourceAndDestinationAccountCurrencyCode(
                        toAccCurrencyCode = toAccCurrencyCode,
                        fromAccCurrencyCode = fromAccCurrencyCode
                    ) && !resetRate
                ) {
                    customExchangeRateState.exchangeRate
                } else {
                    exchangeRatesLogic.convertAmount(
                        baseCurrency = baseUserCurrency,
                        amount = 1.0,
                        fromCurrency = fromAccCurrencyCode,
                        toCurrency = toAccCurrencyCode
                    )
                }

            val amount = amt ?: amount

            val customTransferExchangeRateState = CustomExchangeRateState(
                showCard = true,
                toCurrencyCode = toAccCurrencyCode,
                fromCurrencyCode = fromAccCurrencyCode,
                exchangeRate = exRate,
                convertedAmount = exRate * amount
            )

            customExchangeRateState = customTransferExchangeRateState
            uiThread {
                saveIfEditMode()
            }
        }
    }

    private fun isCustomExchangeRateCurrencyCodeMatchingWithSourceAndDestinationAccountCurrencyCode(
        toAccCurrencyCode: String,
        fromAccCurrencyCode: String
    ): Boolean {
        return customExchangeRateState.showCard &&
                toAccCurrencyCode == customExchangeRateState.toCurrencyCode &&
                fromAccCurrencyCode == customExchangeRateState.fromCurrencyCode
    }

    private fun saveIfEditMode(closeScreen: Boolean = false) {
        if (editMode) {
            hasChanges = true

            save(closeScreen)
        }
    }

    private suspend fun getAllTags(): ImmutableList<Tag> =
        tagRepository.findAll().toImmutableList()

    private fun onTagSaved(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            NotBlankTrimmedString.from(name.toLowerCaseLocal())
                .onRight {
                    val tag = with(tagMapper) { createNewTag(name = it) }
                    tagRepository.save(tag)
                    this@EditTransactionViewModel.tags = getAllTags()
                }

            saveIfEditMode()
        }
    }

    private fun associateTagToTransaction(selectedTag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            val associatedId = AssociationId(loadedTransaction().id)
            tagRepository.associateTagToEntity(associatedId, selectedTag.id)
            transactionAssociatedTags =
                tagRepository.findByAssociatedId(associatedId).map(Tag::id).toImmutableList()
        }
    }

    private fun associateTagToTransaction(selectedTag: Tag, id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val associatedId = AssociationId(id)
            tagRepository.associateTagToEntity(associatedId, selectedTag.id)
            transactionAssociatedTags =
                tagRepository.findByAssociatedId(associatedId).map(Tag::id).toImmutableList()
        }
    }

    private fun removeTagAssociation(selectedTag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            val associatedId = AssociationId(loadedTransaction().id)
            tagRepository.removeTagAssociation(associatedId, selectedTag.id)
            transactionAssociatedTags =
                tagRepository.findByAssociatedId(associatedId).map(Tag::id).toImmutableList()
        }
    }

    private fun searchTag(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tagSearchJob?.cancelAndJoin()
            delay(tagSearchDebounceTimeInMills) // Debounce effect
            tagSearchJob = launch(Dispatchers.IO) {
                NotBlankTrimmedString.from(query.toLowerCaseLocal())
                    .onRight {
                        tags =
                            tagRepository.findByText(text = it.value).toImmutableList()
                    }
                    .onLeft {
                        tags = tagRepository.findAll().toImmutableList()
                    }
            }
        }
    }

    private fun deleteTag(selectedTag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.deleteById(selectedTag.id)
            tags = tagRepository.findAll().toImmutableList()
        }
    }

    private fun updateTagInformation(newTag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.save(newTag)
            tags = tagRepository.findAll().toImmutableList()
        }
    }

    private suspend fun sortCategories(): ImmutableList<Category> {
        val categories = categoryRepository.findAll()
        return if (shouldSortCategoriesAscending()) {
            categories.sortedBy { it.name.value }.toImmutableList()
        } else {
            categories.toImmutableList()
        }
    }

    private suspend fun shouldSortCategoriesAscending(): Boolean {
        return features.sortCategoriesAscending.isEnabled(context)
    }
}
