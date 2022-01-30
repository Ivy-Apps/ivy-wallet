package com.ivy.wallet.ui.loandetails

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.computationThread
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.logic.LoanCreator
import com.ivy.wallet.logic.LoanRecordCreator
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.*
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.uploader.TransactionUploader
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionUploader: TransactionUploader,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val loanCreator: LoanCreator,
    private val loanRecordCreator: LoanRecordCreator,
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyContext
) : ViewModel() {

    private val _baseCurrency = MutableStateFlow("")
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _loan = MutableStateFlow<Loan?>(null)
    val loan = _loan.asStateFlow()

    private val _loanRecords = MutableStateFlow(emptyList<LoanRecord>())
    val loanRecords = _loanRecords.asStateFlow()

    private val _amountPaid = MutableStateFlow(0.0)
    val amountPaid = _amountPaid.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private var associatedTransaction: Transaction? = null

    private val _createLoanTransaction = MutableStateFlow(false)
    val createLoanTransaction = _createLoanTransaction.asStateFlow()


    fun start(screen: Screen.LoanDetails) {
        load(loanId = screen.loanId)
    }

    private fun load(loanId: UUID) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _baseCurrency.value = ioThread {
                settingsDao.findFirst().currency
            }

            _loan.value = ioThread {
                loanDao.findById(id = loanId)
            }

            _loanRecords.value = ioThread {
                loanRecordDao.findAllByLoanId(loanId = loanId)
            }

            _amountPaid.value = computationThread {
                loanRecords.value.sumOf {
                    it.amount
                }
            }

            _createLoanTransaction.value = false

            _accounts.value = ioThread {
                accountDao.findAll()
            }!!

            associatedTransaction = ioThread {
                transactionDao.findLoanTransaction(loanId = loan.value!!.id)
            }

            associatedTransaction?.let {
                val account = ioThread {
                    accountDao.findById(it.accountId)
                }!!
                _selectedAccount.value = account
                _createLoanTransaction.value = true
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoan(loan: Loan) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loanCreator.edit(loan) {
                load(loanId = it.id)
            }

            updateAssociatedTransaction(loan)

            TestIdlingResource.decrement()
        }
    }

    fun deleteLoan() {
        val loan = loan.value ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanCreator.delete(loan) {
                //close screen
                ivyContext.back()
            }

            deleteTransaction()

            TestIdlingResource.decrement()
        }
    }

    fun createLoanRecord(data: CreateLoanRecordData) {
        val loanId = loan.value?.id ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanRecordCreator.create(
                loanId = loanId,
                data = data
            ) {
                load(loanId = loanId)
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoanRecord(loanRecord: LoanRecord) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loanRecordCreator.edit(loanRecord) {
                load(loanId = it.loanId)
            }

            TestIdlingResource.decrement()
        }
    }

    fun deleteLoanRecord(loanRecord: LoanRecord) {
        val loanId = loan.value?.id ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanRecordCreator.delete(loanRecord) {
                load(loanId = loanId)
            }

            TestIdlingResource.decrement()
        }
    }


    private suspend fun updateAssociatedTransaction(loan: Loan) {
        if (createLoanTransaction.value && associatedTransaction != null) {
            val updatedTransaction = associatedTransaction!!.copy(
                accountId = selectedAccount.value?.id ?: associatedTransaction!!.accountId,
                title = loan.name,
                amount = loan.amount,
                type = if (loan.type == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE
            )
            ioThread {
                transactionDao.save(updatedTransaction)
            }
        } else if (createLoanTransaction.value && associatedTransaction == null) {
            createLoanTransaction(data = loan, selectedAccount = selectedAccount.value)
        } else {
            deleteTransaction()
        }
    }

    fun onAccountSelected(account: Account) {
        _selectedAccount.value = account
    }

    fun onLoanTransactionChecked(boolean: Boolean) {
        _createLoanTransaction.value = boolean
        if (_createLoanTransaction.value && associatedTransaction == null && _accounts.value.isNotEmpty()) {
            _selectedAccount.value = accounts.value[0]
        }
    }

    private suspend fun createLoanTransaction(
        data: Loan,
        selectedAccount: Account?,
    ) {
        if (selectedAccount == null)
            return

        var loanCategoryExistence = false

        val transType =
            if (data.type == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE

        val categoryList = ioThread {
            categoryDao.findAll().filter { category ->
                return@filter category.name.lowercase(Locale.ENGLISH).contains("loan")
            }
        }

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
            loanId = data.id,
        )

        ioThread {
            if (loanCategoryExistence)
                categoryDao.save(category)
            transactionDao.save(transaction)
        }
    }

    private suspend fun deleteTransaction() {
        ioThread {
            associatedTransaction?.let {
                transactionDao.flagDeleted(it.id)
            }

            associatedTransaction?.let {
                transactionUploader.delete(it.id)
            }
        }
    }
}