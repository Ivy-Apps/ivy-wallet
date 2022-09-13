package com.ivy.wallet.ui.planned.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.planned.IntervalType
import com.ivy.data.planned.PlannedPaymentRule
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.screens.EditPlanned
import com.ivy.temp.event.AccountsUpdatedEvent
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.logic.CategoryCreator
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsGenerator
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.item.TransactionSync
import com.ivy.wallet.domain.deprecated.sync.uploader.PlannedPaymentRuleUploader
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditPlannedViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val nav: Navigation,
    private val transactionSync: TransactionSync,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val plannedPaymentRuleUploader: PlannedPaymentRuleUploader,
    private val plannedPaymentsGenerator: PlannedPaymentsGenerator,
    private val categoryCreator: CategoryCreator,
    private val accountCreator: AccountCreator,
    private val accountsAct: AccountsActOld,
    private val categoriesAct: CategoriesActOld
) : ViewModel() {

    private val _transactionType = MutableLiveData<TrnTypeOld>()
    val transactionType = _transactionType

    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate = _startDate.asLiveData()

    private val _intervalN = MutableLiveData<Int?>()
    val intervalN = _intervalN.asLiveData()

    private val _intervalType = MutableLiveData<IntervalType?>()
    val intervalType = _intervalType.asLiveData()

    private val _oneTime = MutableLiveData<Boolean>(false)
    val oneTime = _oneTime.asLiveData()

    private val _initialTitle = MutableLiveData<String?>()
    val initialTitle = _initialTitle.asLiveData()

    private val _currency = MutableLiveData<String>()
    val currency = _currency.asLiveData()

    private val _description = MutableLiveData<String?>()
    val description = _description.asLiveData()

    private val _accounts = MutableLiveData<List<AccountOld>>()
    val accounts = _accounts.asLiveData()

    private val _categories = MutableLiveData<List<CategoryOld>>()
    val categories = _categories.asLiveData()

    private val _account = MutableLiveData<AccountOld>()
    val account = _account.asLiveData()

    private val _category = MutableLiveData<CategoryOld?>()
    val category = _category.asLiveData()

    private val _amount = MutableLiveData(0.0)
    val amount = _amount.asLiveData()

    private var loadedRule: PlannedPaymentRule? = null
    private var editMode = false

    var title: String? = null

    fun start(screen: EditPlanned) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            editMode = screen.plannedPaymentRuleId != null

            val accounts = accountsAct(Unit)
            if (accounts.isEmpty()) {
                nav.back()
                return@launch
            }
            _accounts.value = accounts

            _categories.value = categoriesAct(Unit)!!

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

            TestIdlingResource.decrement()
        }
    }

    private suspend fun display(rule: PlannedPaymentRule) {
        this.title = rule.title

        _transactionType.value = rule.type
        _startDate.value = rule.startDate
        _intervalN.value = rule.intervalN
        _oneTime.value = rule.oneTime
        _intervalType.value = rule.intervalType
        _initialTitle.value = rule.title
        _description.value = rule.description
        val selectedAccount = ioThread { accountDao.findById(rule.accountId)!!.toDomain() }
        _account.value = selectedAccount
        _category.value = rule.categoryId?.let {
            ioThread { categoryDao.findById(rule.categoryId!!)?.toDomain() }
        }
        _amount.value = rule.amount

        updateCurrency(account = selectedAccount)
    }

    private suspend fun updateCurrency(account: AccountOld) {
        _currency.value = account.currency ?: baseCurrency()
    }

    private suspend fun baseCurrency(): String =
        ioThread { settingsDao.findFirstSuspend().currency }


    fun onRuleChanged(
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
        _startDate.value = startDate
        _intervalN.value = intervalN
        _intervalType.value = intervalType
        _oneTime.value = oneTime

        saveIfEditMode()
    }

    fun onAmountChanged(newAmount: Double) {
        loadedRule = loadedRule().copy(
            amount = newAmount
        )
        _amount.value = newAmount

        saveIfEditMode()
    }

    fun onTitleChanged(newTitle: String?) {
        loadedRule = loadedRule().copy(
            title = newTitle
        )
        this.title = newTitle

        saveIfEditMode()
    }

    fun onDescriptionChanged(newDescription: String?) {
        loadedRule = loadedRule().copy(
            description = newDescription
        )
        _description.value = newDescription

        saveIfEditMode()
    }

    fun onCategoryChanged(newCategory: CategoryOld?) {
        loadedRule = loadedRule().copy(
            categoryId = newCategory?.id
        )
        _category.value = newCategory

        saveIfEditMode()
    }

    fun onAccountChanged(newAccount: AccountOld) {
        loadedRule = loadedRule().copy(
            accountId = newAccount.id
        )
        _account.value = newAccount

        viewModelScope.launch {
            updateCurrency(account = newAccount)
        }

        saveIfEditMode()
    }

    fun onSetTransactionType(newTransactionType: TrnTypeOld) {
        loadedRule = loadedRule().copy(
            type = newTransactionType
        )
        _transactionType.value = newTransactionType

        saveIfEditMode()
    }

    private fun saveIfEditMode() {
        if (editMode) {
            save(false)
        }
    }

    fun save(closeScreen: Boolean = true) {
        if (!validate()) {
            return
        }

        viewModelScope.launch {
            TestIdlingResource.increment()

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
                        amount = amount.value ?: error("no amount"),

                        isSynced = false
                    )

                    plannedPaymentRuleDao.save(loadedRule().toEntity())
                    plannedPaymentsGenerator.generate(loadedRule())
                }

                if (closeScreen) {
                    nav.back()

                    ioThread {
                        plannedPaymentRuleUploader.sync(loadedRule())
                        transactionSync.sync()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            TestIdlingResource.decrement()
        }
    }

    private fun validate(): Boolean {
        if (transactionType.value == TrnTypeOld.TRANSFER) {
            return false
        }

        if (amount.value == 0.0) {
            return false
        }

        return if (oneTime.value == true) validateOneTime() else validateRecurring()
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

    fun delete() {
        viewModelScope.launch {
            ioThread {
                loadedRule?.let {
                    plannedPaymentRuleDao.flagDeleted(it.id)
                    transactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(
                        recurringRuleId = it.id
                    )
                }
                nav.back()

                loadedRule?.let {
                    plannedPaymentRuleUploader.delete(it.id)
                    transactionSync.sync()
                }
            }
        }
    }

    fun createCategory(data: CreateCategoryData) {
        viewModelScope.launch {
            categoryCreator.createCategory(data) {
                _categories.value = categoriesAct(Unit)!!

                onCategoryChanged(it)
            }
        }
    }

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            accountCreator.createAccount(data) {
                EventBus.getDefault().post(AccountsUpdatedEvent())
                _accounts.value = accountsAct(Unit)!!
            }
        }
    }

    private fun reset() {
        loadedRule = null

        _initialTitle.value = null
        _description.value = null
        _category.value = null
    }

    private fun loadedRule() = loadedRule ?: error("Loaded transaction is null")
}