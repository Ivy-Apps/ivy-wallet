package com.ivy.wallet.ui.loandetails

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.computationThread
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.event.AccountsUpdatedEvent
import com.ivy.wallet.logic.AccountCreator
import com.ivy.wallet.logic.LoanCreator
import com.ivy.wallet.logic.LoanRecordCreator
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateLoanRecordData
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.*
import com.ivy.wallet.persistence.dao.*
import com.ivy.wallet.sync.uploader.TransactionUploader
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.loan.data.DisplayLoanRecord
import com.ivy.wallet.ui.theme.components.IVY_COLOR_PICKER_COLORS_FREE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionUploader: TransactionUploader,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val accountCreator: AccountCreator,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val loanCreator: LoanCreator,
    private val loanRecordCreator: LoanRecordCreator,
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyContext
) : ViewModel() {

    private var defaultCurrencyCode = ""

    private val _baseCurrency = MutableStateFlow("")
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _loan = MutableStateFlow<Loan?>(null)
    val loan = _loan.asStateFlow()

    private val _loanRecords = MutableStateFlow(emptyList<LoanRecord>())
    val loanRecords = _loanRecords.asStateFlow()

    private val _displayLoanRecords = MutableStateFlow(emptyList<DisplayLoanRecord>())
    val displayLoanRecords = _displayLoanRecords.asStateFlow()

    private val _amountPaid = MutableStateFlow(0.0)
    val amountPaid = _amountPaid.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _loanAmountPaid = MutableStateFlow(0.0)
    val loanAmountPaid = _loanAmountPaid.asStateFlow()

    private val _selectedLoanAccount = MutableStateFlow<Account?>(null)
    val selectedLoanAccount = _selectedLoanAccount.asStateFlow()

    private var associatedTransaction: Transaction? = null

    private val _createLoanTransaction = MutableStateFlow(false)
    val createLoanTransaction = _createLoanTransaction.asStateFlow()


    fun start(screen: Screen.LoanDetails) {
        load(loanId = screen.loanId)
    }

    private fun load(loanId: UUID) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }

            _baseCurrency.value = defaultCurrencyCode

            _accounts.value = ioThread {
                accountDao.findAll()
            }

            _loan.value = ioThread {
                loanDao.findById(id = loanId)
            }

            loan.value?.let { loan ->
                _selectedLoanAccount.value = accounts.value.find {
                    loan.accountId == it.id
                }

                _selectedLoanAccount.value?.let { acc ->
                    _baseCurrency.value = acc.currency ?: defaultCurrencyCode
                }
            }

            computationThread {
                _displayLoanRecords.value =
                    ioThread { loanRecordDao.findAllByLoanId(loanId = loanId) }.map {
                        val trans = ioThread {
                            transactionDao.findLoanRecordTransaction(
                                it.id
                            )
                        }

                        val account = findAccount(
                            accounts = accounts.value,
                            accountId = it.accountId,
                        ) ?: findAccount(accounts.value, trans?.accountId)

                        DisplayLoanRecord(
                            it,
                            account = account,
                            loanRecordTransaction = trans != null,
                            currencyCode = account?.currency ?: defaultCurrencyCode
                        )
                    }
            }

            computationThread {
                //Using a local variable to calculate the amount and then reassigning to
                // the global variable to reduce the amount of compose re-draws
                var amtPaid = 0.0
                var loanInterestAmtPaid = 0.0
                displayLoanRecords.value.forEach {
                    val convertedAmount = exchangeRatesLogic.convertAmount(
                        baseCurrency = defaultCurrencyCode,
                        amount = it.loanRecord.amount,
                        fromCurrency = it.account?.currency ?: defaultCurrencyCode,
                        toCurrency = _selectedLoanAccount.value?.currency ?: defaultCurrencyCode
                    )
                    if (!it.loanRecord.interest) {
                        amtPaid += convertedAmount
                    } else
                        loanInterestAmtPaid += convertedAmount
                }

                _amountPaid.value = amtPaid
                _loanAmountPaid.value = loanInterestAmtPaid
            }

            associatedTransaction = ioThread {
                transactionDao.findLoanTransaction(loanId = loan.value!!.id)
            }

            associatedTransaction?.let {
                _createLoanTransaction.value = true
            } ?: run {
                _createLoanTransaction.value = false
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoan(loan: Loan, createLoanTransaction: Boolean = false) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loanCreator.edit(loan) {
                load(loanId = it.id)
            }

            updateAssociatedTransaction(
                createTransaction = createLoanTransaction,
                loanId = loan.id,
                amount = loan.amount,
                loanType = loan.type,
                selectedAccountId = loan.accountId,
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
            ioThread {
                val transactions = transactionDao.findAllByLoanId(loanId = loan.id)
                transactions.forEach { trans ->
                    deleteTransaction(trans)
                }
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

            if (data.createLoanRecordTransaction && loanRecordUUID != null) {
                updateAssociatedTransaction(
                    createTransaction = data.createLoanRecordTransaction,
                    loanType = localLoan.type,
                    amount = data.amount,
                    title = data.note,
                    time = data.dateTime,
                    loanRecordId = loanRecordUUID,
                    loanId = loan.value!!.id,
                    selectedAccountId = data.account?.id,
                    isLoanRecord = true,
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoanRecord(loanRecord: LoanRecord, createLoanRecordTransaction: Boolean = false) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loanRecordCreator.edit(loanRecord) {
                load(loanId = it.loanId)
            }

            ioThread {
                val transaction = transactionDao.findLoanRecordTransaction(loanRecord.id)
                updateAssociatedTransaction(
                    createTransaction = createLoanRecordTransaction,
                    loanRecordId = loanRecord.id,
                    loanId = loan.value!!.id,
                    amount = loanRecord.amount,
                    loanType = loan.value!!.type,
                    selectedAccountId = loanRecord.accountId,
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
                val transaction = transactionDao.findLoanRecordTransaction(loanRecord.id)
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
        selectedAccountId: UUID?,
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
                selectedAccountId = selectedAccountId,
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
                selectedAccountId = selectedAccountId,
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

    fun onLoanTransactionChecked(boolean: Boolean) {
        _createLoanTransaction.value = boolean
    }

    private suspend fun createMainTransaction(
        loanRecordId: UUID? = null,
        amount: Double,
        loanType: LoanType,
        loanId: UUID,
        selectedAccountId: UUID?,
        title: String? = null,
        categoryId: UUID? = null,
        time: LocalDateTime = timeNowUTC(),
        isLoanRecord: Boolean = false,
        transaction: Transaction? = null
    ) {
        if (selectedAccountId == null)
            return

        val transType = if (isLoanRecord)
            if (loanType == LoanType.BORROW) TransactionType.EXPENSE else TransactionType.INCOME
        else
            if (loanType == LoanType.BORROW) TransactionType.INCOME else TransactionType.EXPENSE

        val transCategoryId: UUID? = getCategoryId(existingCategoryId = categoryId)

        val modifiedTransaction: Transaction = transaction?.copy(
            loanId = loanId,
            loanRecordId = if (isLoanRecord) loanRecordId else null,
            amount = amount,
            type = transType,
            accountId = selectedAccountId,
            title = title,
            categoryId = transCategoryId,
            dateTime = time
        )
            ?: Transaction(
                accountId = selectedAccountId,
                type = transType,
                amount = amount,
                dateTime = time,
                categoryId = transCategoryId,
                title = title,
                loanId = loanId,
                loanRecordId = if (isLoanRecord) loanRecordId else null
            )

        ioThread {
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

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                EventBus.getDefault().post(AccountsUpdatedEvent())
                _accounts.value = ioThread { accountDao.findAll() }
            }

            TestIdlingResource.decrement()
        }
    }

    private fun findAccount(
        accounts: List<Account>,
        accountId: UUID?,
    ): Account? {
        return accountId?.let { uuid ->
            accounts.find { acc ->
                acc.id == uuid
            }
        }
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