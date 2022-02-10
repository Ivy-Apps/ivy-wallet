package com.ivy.wallet.ui.loan

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.*
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.logic.LoanCreator
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.item.LoanSync
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
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val settingsDao: SettingsDao,
    private val loanSync: LoanSync,
    private val loanCreator: LoanCreator
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _loans = MutableStateFlow(emptyList<DisplayLoan>())
    val loans = _loans.asStateFlow()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts = _accounts.asLiveData()

    private val _selectedAccount = MutableLiveData<Account>()
    val selectedAccount = _selectedAccount.asLiveData()

    private val _createLoanTransaction = MutableStateFlow(false)
    val createLoanTransaction = _createLoanTransaction.asStateFlow()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            _loans.value = ioThread {
                loanDao.findAll()
                    .map { loan ->
                        DisplayLoan(
                            loan = loan,
                            amountPaid = loanRecordDao.findAllByLoanId(loanId = loan.id)
                                .sumOf { loanRecord ->
                                    if (!loanRecord.interest) loanRecord.amount else 0.0
                                }
                        )
                    }
            }
            initialiseAccounts()

            TestIdlingResource.decrement()
        }
    }

    private suspend fun initialiseAccounts() {
        val accounts = ioThread { accountDao.findAll() }!!
        _accounts.value = accounts
        _selectedAccount.value = defaultAccountId(accounts)
        _selectedAccount.value?.let {
            _baseCurrencyCode.value = it.currency ?: baseCurrencyCode.value
        }
    }

    fun createLoan(data: CreateLoanData, selectedAccount: Account? = null) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val uuid = loanCreator.create(data) {
                start()
            }

            createLoanTransaction(data, selectedAccount, uuid)

            TestIdlingResource.decrement()
        }
    }

    private suspend fun createLoanTransaction(
        data: CreateLoanData,
        selectedAccount: Account?,
        loanId: UUID?
    ) {
        if (selectedAccount == null)
            return

        val transType =
            if (data.type == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE

        val categoryList = ioThread {
            categoryDao.findAll().filter { category ->
                return@filter category.name.lowercase(Locale.ENGLISH).contains("loan")
            }
        }
        var loanCategoryExistence = false
        val category = if (categoryList.isEmpty()) {
            loanCategoryExistence = true
            Category("Loans", color = IVY_COLOR_PICKER_COLORS_FREE[4].toArgb(), icon = "loan")
        } else
            categoryList.first()

        val transaction = Transaction(
            accountId = selectedAccount.id,
            type = transType,
            amount = data.amount,
            dateTime = timeNowUTC(),
            categoryId = category.id,
            title = data.name,
            loanId = loanId,
        )

        ioThread {
            if (loanCategoryExistence)
                categoryDao.save(category)
            transactionDao.save(transaction)
        }
    }

    fun onAccountSelected(account: Account) {
        viewModelScope.launch {

            _selectedAccount.value = account
            _baseCurrencyCode.value = selectedAccount.value?.currency ?: getDefaultFIATCurrency().currencyCode

            Log.d("GGGG", _baseCurrencyCode.value)
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

    fun onLoanTransactionChecked(boolean: Boolean) {
        _createLoanTransaction.value = boolean
    }

    private suspend fun defaultAccountId(
        accounts: List<Account>,
    ): Account? {

        val lastSelectedId = sharedPrefs.getString(SharedPrefs.LAST_SELECTED_ACCOUNT_ID, null)
            ?.let { UUID.fromString(it) }

        lastSelectedId?.let { uuid ->
            return accounts.find { it.id == uuid }
        }

        return null
    }

    fun onBaseCurrencyChanged(currencyCode :String){
        _baseCurrencyCode.value = currencyCode
    }
}