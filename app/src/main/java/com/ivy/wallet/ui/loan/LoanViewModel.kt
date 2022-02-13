package com.ivy.wallet.ui.loan

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.*
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.logic.LoanCreator
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.item.LoanSync
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.loan.data.DisplayLoan
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val accountCreator: AccountCreator,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val settingsDao: SettingsDao,
    private val loanSync: LoanSync,
    private val loanCreator: LoanCreator,
    private val ivyContext: IvyContext
) : ViewModel() {

    private var defaultCurrencyCode = ""

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _loans = MutableStateFlow(emptyList<DisplayLoan>())
    val loans = _loans.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _selectedAccount = MutableLiveData<Account>()
    val selectedAccount = _selectedAccount.asLiveData()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }

            _baseCurrencyCode.value = defaultCurrencyCode

            initialiseAccounts()

            _loans.value = ioThread {
                loanDao.findAll()
                    .map { loan ->
                        DisplayLoan(
                            loan = loan,
                            amountPaid = calculateAmountPaid(loan),
                            currencyCode = findCurrencyCode(accounts.value, loan.accountId)
                        )
                    }
            }

            TestIdlingResource.decrement()
        }
    }

    private suspend fun initialiseAccounts() {
        val accounts = ioThread { accountDao.findAll() }
        _accounts.value = accounts
        _selectedAccount.value = defaultAccountId(accounts)
        _selectedAccount.value?.let {
            _baseCurrencyCode.value = it.currency ?: defaultCurrencyCode
        }
    }

    fun createLoan(data: CreateLoanData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val uuid = loanCreator.create(data) {
                start()
            }

            createLoanTransaction(data, data.account, uuid)

            TestIdlingResource.decrement()
        }
    }

    private suspend fun createLoanTransaction(
        data: CreateLoanData,
        selectedAccount: Account?,
        loanId: UUID?
    ) {
        if (selectedAccount == null || !data.createLoanTransaction)
            return

        val transType =
            if (data.type == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE

        val categoryId = getCategoryId()

        val transaction = Transaction(
            accountId = selectedAccount.id,
            type = transType,
            amount = data.amount,
            dateTime = timeNowUTC(),
            categoryId = categoryId,
            title = data.name,
            loanId = loanId,
        )

        ioThread {
            transactionDao.save(transaction)
        }
    }

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                EventBus.getDefault().post(AccountsUpdatedEvent())
                _accounts.value = ioThread { accountDao.findAll() }!!
            }

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<DisplayLoan>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.forEachIndexed { index, item ->
                    loanDao.save(
                        item.loan.copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            ioThread {
                loanSync.sync()
            }

            TestIdlingResource.decrement()
        }
    }

    private fun defaultAccountId(
        accounts: List<Account>,
    ): Account? {

        val lastSelectedId =
            sharedPrefs.getString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, null)?.let {
                UUID.fromString(it)
            }

        lastSelectedId?.let { uuid ->
            return accounts.find { it.id == uuid }
        } ?: run {
            return if (accounts.isNotEmpty()) accounts[0] else null
        }
    }

    private fun findCurrencyCode(accounts: List<Account>, accountId: UUID?): String {
        return accountId?.let {
            accounts.find { account -> account.id == it }?.currency
        } ?: defaultCurrencyCode
    }

    private suspend fun calculateAmountPaid(loan: Loan): Double {
        val loanRecords = ioThread { loanRecordDao.findAllByLoanId(loanId = loan.id) }
        var amount = 0.0

        loanRecords.forEach { loanRecord ->
            if (!loanRecord.interest) {
                val convertedAmount = loanRecord.convertedAmount ?: loanRecord.amount
                amount += convertedAmount
            }
        }

        return amount
    }

    private suspend fun getCategoryId(existingCategoryId: UUID? = null): UUID? {
        if (existingCategoryId != null)
            return existingCategoryId

        val categoryList = ioThread {
            categoryDao.findAll()
        }

        var addCategoryToDb = false

        val loanCategory = categoryList.find { category ->
            category.name.lowercase(Locale.ENGLISH).contains("loan")
        } ?: if (ivyContext.isPremium || categoryList.size < 12) {
            addCategoryToDb = true
            Category(
                "Loans",
                color = IVY_COLOR_PICKER_COLORS_FREE[4].toArgb(),
                icon = "loan"
            )
        } else null

        if (addCategoryToDb)
            ioThread {
                loanCategory?.let {
                    categoryDao.save(it)
                }
            }

        return loanCategory?.id
    }
}