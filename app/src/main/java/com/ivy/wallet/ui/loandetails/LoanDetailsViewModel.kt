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
import java.time.LocalDateTime
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

    private val _selectedLoanAccount = MutableStateFlow<Account?>(null)
    val selectedLoanAccount = _selectedLoanAccount.asStateFlow()

    private val _selectedLoanRecordAccount = MutableStateFlow<Account?>(null)
    val selectedLoanRecordAccount = _selectedLoanRecordAccount.asStateFlow()

    private var associatedTransaction: Transaction? = null

    private val _createLoanTransaction = MutableStateFlow(false)
    val createLoanTransaction = _createLoanTransaction.asStateFlow()

    private val _createLoanRecordTransaction = MutableStateFlow(false)
    val createLoanRecordTransaction = _createLoanRecordTransaction.asStateFlow()

    private val _loanInterest = MutableStateFlow(false)
    val loanInterest = _loanInterest.asStateFlow()

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
            }

            associatedTransaction = ioThread {
                transactionDao.findLoanTransaction(loanId = loan.value!!.id)
            }

            associatedTransaction?.let { trans ->
                _selectedLoanAccount.value = accounts.value.find { account ->
                    trans.accountId == account.id
                }
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

            updateAssociatedTransaction(
                createTransaction = createLoanTransaction.value,
                loanId = loan.id,
                amount = loan.amount,
                loanType = loan.type,
                selectedAccount = selectedLoanAccount.value,
                title = loan.name,
                isLoanRecord = false,
                transaction = associatedTransaction,
                time = associatedTransaction?.dateTime
            )

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

            associatedTransaction?.let {
                deleteTransaction(it)
            }

            TestIdlingResource.decrement()
        }
    }

    fun createLoanRecord(data: CreateLoanRecordData) {
        if (loan.value == null) return
        val loanId = loan.value?.id ?: return
        val localLoan = loan.value!!

        viewModelScope.launch {
            TestIdlingResource.increment()

            val loanRecordUUID = loanRecordCreator.create(
                loanId = loanId,
                data = data
            ) {
                load(loanId = loanId)
            }

            if (createLoanRecordTransaction.value && loanRecordUUID != null) {

                updateAssociatedTransaction(
                    createTransaction = createLoanRecordTransaction.value,
                    loanType = localLoan.type,
                    amount = data.amount,
                    title = data.note,
                    time = data.dateTime,
                    loanRecordId = loanRecordUUID,
                    loanId = loan.value!!.id,
                    selectedAccount = selectedLoanRecordAccount.value,
                    isLoanRecord = true,
                )
//                createMainTransaction(
//                    loanType = localLoan.type,
//                    amount = data.amount,
//                    title = data.note,
//                    time = data.dateTime,
//                    loanRecordId = loanRecordUUID,
//                    loanId = loan.value!!.id,
//                    selectedAccount = selectedLoanAccount.value,
//                    isLoanRecord = true
//                )
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

            ioThread {
                val transaction = transactionDao.findLoanRecordTransaction(loanRecord.id)
                updateAssociatedTransaction(
                    createTransaction = createLoanRecordTransaction.value,
                    loanRecordId = loanRecord.id,
                    loanId = loan.value!!.id,
                    amount = loanRecord.amount,
                    loanType = loan.value!!.type,
                    selectedAccount = selectedLoanAccount.value,
                    title = loanRecord.note,
                    time = loanRecord.dateTime,
                    isLoanRecord = true,
                    transaction = transaction
                )
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
            ioThread {
                val transaction = transactionDao.findLoanRecordTransaction(loanRecord.loanId)
                deleteTransaction(transaction)
            }
            TestIdlingResource.decrement()
        }
    }


    private suspend fun updateAssociatedTransaction(
        createTransaction: Boolean,
        loanRecordId: UUID? = null,
        loanId: UUID,
        amount: Double,
        loanType: LoanType,
        selectedAccount: Account?,
        title: String? = null,
        category: Category? = null,
        time: LocalDateTime? = null,
        isLoanRecord: Boolean = false,
        transaction: Transaction? = null,
    ) {
        if (isLoanRecord && loanRecordId == null)
            return

        if (createTransaction && transaction != null) {
            createMainTransaction(
                loanRecordId = loanRecordId,
                loanId = loanId,
                amount = amount,
                loanType = loanType,
                selectedAccount = selectedAccount,
                title = title ?: transaction.title,
                categoryId = category?.id ?: transaction.categoryId,
                time = time ?: transaction.dateTime ?: timeNowUTC(),
                isLoanRecord = isLoanRecord,
                transaction = transaction
            )
        } else if (createTransaction && transaction == null) {
            createMainTransaction(
                loanRecordId = loanRecordId,
                loanId = loanId,
                amount = amount,
                loanType = loanType,
                selectedAccount = selectedAccount,
                title = title,
                categoryId = category?.id,
                time = time ?: timeNowUTC(),
                isLoanRecord = isLoanRecord,
                transaction = transaction
            )
        } else {
            deleteTransaction(transaction = transaction)
        }
    }

    private fun changeAccount(account: Account, isLoan: Boolean = true) {
        if (isLoan)
            _selectedLoanAccount.value = account
        else
            _selectedLoanRecordAccount.value = account
    }

    fun onLoanAccountSelected(account: Account) {
        changeAccount(account, true)
    }

    fun onLoanRecordAccountSelected(account: Account) {
        changeAccount(account, false)
    }

    fun onLoanInterestClicked(boolean: Boolean) {
        _loanInterest.value = boolean
    }

    fun onLoanTransactionChecked(boolean: Boolean) {
        _createLoanTransaction.value = boolean
        if (_createLoanTransaction.value && associatedTransaction == null && _accounts.value.isNotEmpty()) {
            _selectedLoanAccount.value = accounts.value[0]
        }
    }

    fun onLoanRecordTransactionChecked(boolean: Boolean) {
        _createLoanRecordTransaction.value = boolean
    }

    private suspend fun createMainTransaction(
        loanRecordId: UUID? = null,
        amount: Double,
        loanType: LoanType,
        loanId: UUID,
        selectedAccount: Account?,
        title: String? = null,
        categoryId: UUID? = null,
        time: LocalDateTime = timeNowUTC(),
        isLoanRecord: Boolean = false,
        transaction: Transaction? = null
    ) {
        if (selectedAccount == null)
            return

        var loanCategoryExistence = false

        val transType = if (isLoanRecord)
            if (loanType == LoanType.BORROW) TransactionType.EXPENSE else TransactionType.INCOME
        else
            if (loanType == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE


        var transCategory: Category? = null
        val transCategoryId: UUID = if (categoryId == null) {
            val categoryList = ioThread {
                categoryDao.findAll().filter { category ->
                    return@filter category.name.lowercase(Locale.ENGLISH).contains("loan")
                }
            }

            if (categoryList.isEmpty()) {
                loanCategoryExistence = true
                transCategory = Category(
                    "Loans",
                    color = IVY_COLOR_PICKER_COLORS_FREE[4].toArgb(),
                    icon = "loan"
                )
                transCategory.id
            } else
                categoryList.first().id
        } else
            categoryId

        val modifiedTransaction: Transaction = transaction?.copy(
            loanId = loanId,
            loanRecordId = if (isLoanRecord) loanRecordId else null,
            amount = amount,
            type = transType,
            accountId = selectedAccount.id,
            title = title,
            categoryId = transCategoryId,
            dateTime = time
        )
            ?: Transaction(
                accountId = selectedAccount.id,
                type = transType,
                amount = amount,
                dateTime = time,
                categoryId = transCategoryId,
                title = title,
                loanId = loanId,
                loanRecordId = if (isLoanRecord) loanRecordId else null
            )

        ioThread {
            if (loanCategoryExistence)
                transCategory?.let {
                    categoryDao.save(it)
                }
            transactionDao.save(modifiedTransaction)
        }
    }

    private suspend fun deleteTransaction(transaction: Transaction?) {
        ioThread {
            transaction?.let {
                transactionDao.flagDeleted(it.id)
            }

            transaction?.let {
                transactionUploader.delete(it.id)
            }
        }
    }

    fun onLoanRecordClicked(uuid: UUID, isLoanInterest: Boolean) {
        viewModelScope.launch {
            val transaction = ioThread {
                transactionDao.findLoanRecordTransaction(uuid)
            }
            transaction?.let { trans ->
                _createLoanRecordTransaction.value = true

                _selectedLoanRecordAccount.value = accounts.value.find { account ->
                    account.id == trans.accountId
                }

            }
            _loanInterest.value = isLoanInterest
        }
    }
}