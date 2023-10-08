package com.ivy.planned.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.model.IntervalType
import com.ivy.domain.ComposeViewModel
import com.ivy.domain.event.AccountUpdatedEvent
import com.ivy.domain.event.EventBus
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsGenerator
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.theme.modal.RecurringRuleModalData
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditPlannedViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val nav: Navigation,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val plannedPaymentsGenerator: PlannedPaymentsGenerator,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val eventBus: EventBus,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
    private val transactionWriter: WriteTransactionDao,
) : ComposeViewModel<EditPlannedScreenState, EditPlannedScreenEvent>() {

    private val transactionType = mutableStateOf(TransactionType.INCOME)
    private val startDate = mutableStateOf<LocalDateTime?>(null)
    private val intervalN = mutableStateOf<Int?>(null)
    private val intervalType = mutableStateOf<IntervalType?>(null)
    private val oneTime = mutableStateOf(false)
    private val initialTitle = mutableStateOf<String?>(null)
    private val description = mutableStateOf<String?>(null)
    private val account = mutableStateOf<Account?>(null)
    private val category = mutableStateOf<Category?>(null)
    private val amount = mutableDoubleStateOf(0.0)
    private val currency = mutableStateOf("")
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categoryModalVisible = mutableStateOf(false)
    private val descriptionModalVisible = mutableStateOf(false)
    private val deleteTransactionModalVisible = mutableStateOf(false)
    private val transactionTypeModalVisible = mutableStateOf(false)
    private val amountModalVisible = mutableStateOf(false)
    private val recurringRuleModalData = mutableStateOf<RecurringRuleModalData?>(null)
    private val categoryModalData = mutableStateOf<CategoryModalData?>(null)
    private val accountModalData = mutableStateOf<AccountModalData?>(null)

    private var loadedRule: PlannedPaymentRule? = null
    private var editMode = false
    private var title: String? = null

    @Composable
    override fun uiState(): EditPlannedScreenState {
        return EditPlannedScreenState(
            currency = getCurrency(),
            categories = getCategories(),
            accounts = getAccounts(),
            transactionType = getTransactionType(),
            startDate = getStartDate(),
            intervalN = getIntervalN(),
            oneTime = getOneTime(),
            account = getAccount(),
            category = getCategory(),
            amount = getAmount(),
            initialTitle = getInitialTitle(),
            description = getDescription(),
            intervalType = getIntervalType(),
            categoryModalVisible = getCategoryModalVisibility(),
            categoryModalData = getCategoryModalData(),
            accountModalData = getAccountModalData(),
            deleteTransactionModalVisible = getDeleteTransactionModalVisibility(),
            descriptionModalVisible = getDescriptionModalVisibility(),
            amountModalVisible = getAmountModalVisibility(),
            transactionTypeModalVisible = getTransactionTypeModalVisibility(),
            recurringRuleModalData = getRecurringRuleModalData()
        )
    }

    @Composable
    private fun getCurrency(): String {
        return currency.value
    }

    @Composable
    private fun getCategories(): ImmutableList<Category> {
        return categories.value
    }

    @Composable
    private fun getAccounts(): ImmutableList<Account> {
        return accounts.value
    }

    @Composable
    private fun getTransactionType(): TransactionType {
        return transactionType.value
    }

    @Composable
    private fun getStartDate(): LocalDateTime? {
        return startDate.value
    }

    @Composable
    private fun getIntervalN(): Int? {
        return intervalN.value
    }

    @Composable
    private fun getIntervalType(): IntervalType? {
        return intervalType.value
    }

    @Composable
    private fun getOneTime(): Boolean {
        return oneTime.value
    }

    @Composable
    private fun getInitialTitle(): String? {
        return initialTitle.value
    }

    @Composable
    private fun getDescription(): String? {
        return description.value
    }

    @Composable
    private fun getAccount(): Account? {
        return account.value
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
    private fun getCategoryModalVisibility(): Boolean {
        return categoryModalVisible.value
    }

    @Composable
    private fun getDescriptionModalVisibility(): Boolean {
        return descriptionModalVisible.value
    }

    @Composable
    private fun getDeleteTransactionModalVisibility(): Boolean {
        return deleteTransactionModalVisible.value
    }

    @Composable
    private fun getTransactionTypeModalVisibility(): Boolean {
        return transactionTypeModalVisible.value
    }

    @Composable
    private fun getAmountModalVisibility(): Boolean {
        return amountModalVisible.value
    }

    @Composable
    private fun getCategoryModalData(): CategoryModalData? {
        return categoryModalData.value
    }

    @Composable
    private fun getAccountModalData(): AccountModalData? {
        return accountModalData.value
    }

    @Composable
    private fun getRecurringRuleModalData(): RecurringRuleModalData? {
        return recurringRuleModalData.value
    }

    override fun onEvent(event: EditPlannedScreenEvent) {
        when (event) {
            is EditPlannedScreenEvent.OnSave -> save()
            is EditPlannedScreenEvent.OnDelete -> delete()
            is EditPlannedScreenEvent.OnSetTransactionType ->
                updateTransactionType(event.newTransactionType)
            is EditPlannedScreenEvent.OnDescriptionChanged ->
                updateDescription(event.newDescription)
            is EditPlannedScreenEvent.OnCreateAccount -> createAccount(event.data)
            is EditPlannedScreenEvent.OnCreateCategory -> createCategory(event.data)
            is EditPlannedScreenEvent.OnAccountChanged -> updateAccount(event.newAccount)
            is EditPlannedScreenEvent.OnAmountChanged -> updateAmount(event.newAmount)
            is EditPlannedScreenEvent.OnTitleChanged -> updateTitle(event.newTitle)
            is EditPlannedScreenEvent.OnRuleChanged ->
                updateRule(event.startDate, event.oneTime, event.intervalN, event.intervalType)
            is EditPlannedScreenEvent.OnCategoryChanged -> updateCategory(event.newCategory)
            is EditPlannedScreenEvent.OnCategoryModalVisible ->
                categoryModalVisible.value = event.visible
            is EditPlannedScreenEvent.OnCategoryModalDataChanged ->
                categoryModalData.value = event.categoryModalData
            is EditPlannedScreenEvent.OnAccountModalDataChanged ->
                accountModalData.value = event.accountModalData
            is EditPlannedScreenEvent.OnDescriptionModalVisible ->
                descriptionModalVisible.value = event.visible
            is EditPlannedScreenEvent.OnTransactionTypeModalVisible ->
                transactionTypeModalVisible.value = event.visible
            is EditPlannedScreenEvent.OnAmountModalVisible ->
                amountModalVisible.value = event.visible
            is EditPlannedScreenEvent.OnDeleteTransactionModalVisible ->
                deleteTransactionModalVisible.value = event.visible
            is EditPlannedScreenEvent.OnRecurringRuleModalDataChanged ->
                recurringRuleModalData.value = event.recurringRuleModalData
        }
    }

    fun start(screen: EditPlannedScreen) {
        viewModelScope.launch {
            transactionType.value = screen.type
            editMode = screen.plannedPaymentRuleId != null

            val accounts = accountsAct(Unit)
            if (accounts.isEmpty()) {
                nav.back()
                return@launch
            }
            this@EditPlannedViewModel.accounts.value = accounts
            categories.value = categoriesAct(Unit)

            reset()

            loadedRule = screen.plannedPaymentRuleId?.let {
                ioThread { plannedPaymentRuleDao.findById(it)!!.toDomain() }
            } ?: PlannedPaymentRule(
                startDate = null,
                intervalN = null,
                intervalType = null,
                oneTime = false,
                type = screen.type,
                amount = screen.amount ?: 0.0,
                accountId = screen.accountId ?: accounts.first().id,
                categoryId = screen.categoryId,
                title = screen.title,
                description = screen.description
            )

            display(loadedRule!!)
        }
    }

    private suspend fun display(rule: PlannedPaymentRule) {
        this.title = rule.title

        transactionType.value = rule.type
        startDate.value = rule.startDate
        intervalN.value = rule.intervalN
        oneTime.value = rule.oneTime
        intervalType.value = rule.intervalType
        initialTitle.value = rule.title
        description.value = rule.description
        val selectedAccount = ioThread { accountDao.findById(rule.accountId)!!.toDomain() }
        account.value = selectedAccount
        category.value = rule.categoryId?.let {
            ioThread { categoryDao.findById(rule.categoryId!!)?.toDomain() }
        }
        amount.doubleValue = rule.amount

        updateCurrency(account = selectedAccount)
    }

    private suspend fun updateCurrency(account: Account) {
        currency.value = account.currency ?: baseCurrency()
    }

    private suspend fun baseCurrency(): String = ioThread { settingsDao.findFirst().currency }

    private fun updateRule(
        startDate: LocalDateTime,
        oneTime: Boolean,
        intervalN: Int?,
        intervalType: IntervalType?
    ) {
        loadedRule = loadedRule().copy(
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime
        )
        this@EditPlannedViewModel.startDate.value = startDate
        this@EditPlannedViewModel.intervalN.value = intervalN
        this@EditPlannedViewModel.intervalType.value = intervalType
        this@EditPlannedViewModel.oneTime.value = oneTime

        saveIfEditMode()
    }

    private fun updateAmount(newAmount: Double) {
        loadedRule = loadedRule().copy(
            amount = newAmount
        )
        this@EditPlannedViewModel.amount.doubleValue = newAmount

        saveIfEditMode()
    }

    private fun updateTitle(newTitle: String?) {
        loadedRule = loadedRule().copy(
            title = newTitle
        )
        this.title = newTitle

        saveIfEditMode()
    }

    private fun updateDescription(newDescription: String?) {
        loadedRule = loadedRule().copy(
            description = newDescription
        )
        this@EditPlannedViewModel.description.value = newDescription

        saveIfEditMode()
    }

    private fun updateCategory(newCategory: Category?) {
        loadedRule = loadedRule().copy(
            categoryId = newCategory?.id
        )
        this@EditPlannedViewModel.category.value = newCategory

        saveIfEditMode()
    }

    private fun updateAccount(newAccount: Account) {
        loadedRule = loadedRule().copy(
            accountId = newAccount.id
        )
        this@EditPlannedViewModel.account.value = newAccount

        viewModelScope.launch {
            updateCurrency(account = newAccount)
        }

        saveIfEditMode()
    }

    private fun updateTransactionType(newTransactionType: TransactionType) {
        loadedRule = loadedRule().copy(
            type = newTransactionType
        )
        this@EditPlannedViewModel.transactionType.value = newTransactionType

        saveIfEditMode()
    }

    private fun saveIfEditMode() {
        if (editMode) {
            save(false)
        }
    }

    private fun save(closeScreen: Boolean = true) {
        if (!validate()) {
            return
        }

        viewModelScope.launch {
            try {
                ioThread {
                    loadedRule = loadedRule().copy(
                        type = transactionType.value ?: error("no transaction type"),
                        startDate = startDate.value ?: error("no startDate"),
                        intervalN = intervalN.value ?: error("no intervalN"),
                        intervalType = intervalType.value ?: error("no intervalType"),
                        categoryId = category.value?.id,
                        accountId = account.value?.id ?: error("no accountId"),
                        title = title?.trim(),
                        description = description.value?.trim(),
                        amount = amount.doubleValue ?: error("no amount"),

                        isSynced = false
                    )

                    plannedPaymentRuleWriter.save(loadedRule().toEntity())
                    plannedPaymentsGenerator.generate(loadedRule())
                }

                if (closeScreen) {
                    nav.back()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validate(): Boolean {
        if (transactionType.value == TransactionType.TRANSFER) {
            return false
        }

        if (amount.doubleValue == 0.0) {
            return false
        }

        return if (oneTime.value) validateOneTime() else validateRecurring()
    }

    private fun validateOneTime(): Boolean {
        return startDate.value != null
    }

    private fun validateRecurring(): Boolean {
        return startDate.value != null &&
                intervalN.value != null &&
                intervalN.value!! > 0 &&
                intervalType.value != null
    }

    private fun delete() {
        viewModelScope.launch {
            deleteTransactionModalVisible.value = false
            ioThread {
                loadedRule?.let {
                    plannedPaymentRuleWriter.flagDeleted(it.id)
                    transactionWriter.flagDeletedByRecurringRuleIdAndNoDateTime(
                        recurringRuleId = it.id
                    )
                }
                nav.back()
            }
        }
    }

    private fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                categories.value = categoriesAct(Unit)

                updateCategory(it)
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

    private fun reset() {
        loadedRule = null

        initialTitle.value = null
        description.value = null
        category.value = null
    }

    private fun loadedRule() = loadedRule ?: error("Loaded transaction is null")
}
