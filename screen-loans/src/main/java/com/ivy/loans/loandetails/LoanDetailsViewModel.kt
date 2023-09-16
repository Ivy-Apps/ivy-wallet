package com.ivy.loans.loandetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.core.data.db.read.AccountDao
import com.ivy.core.data.db.read.LoanDao
import com.ivy.core.data.db.read.LoanRecordDao
import com.ivy.core.data.db.read.SettingsDao
import com.ivy.core.data.db.read.TransactionDao
import com.ivy.core.data.model.Account
import com.ivy.core.data.model.Loan
import com.ivy.core.data.model.LoanRecord
import com.ivy.core.data.model.Transaction
import com.ivy.core.event.AccountUpdatedEvent
import com.ivy.core.event.EventBus
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.utils.computationThread
import com.ivy.legacy.utils.ioThread
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.navigation.LoanDetailsScreen
import com.ivy.navigation.Navigation
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.loan.LoanByIdAct
import com.ivy.wallet.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.logic.LoanCreator
import com.ivy.wallet.domain.deprecated.logic.LoanRecordCreator
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val loanCreator: LoanCreator,
    private val loanRecordCreator: LoanRecordCreator,
    private val settingsDao: SettingsDao,
    private val ivyContext: IvyWalletCtx,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val accountCreator: AccountCreator,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val nav: Navigation,
    private val accountsAct: AccountsAct,
    private val loanByIdAct: LoanByIdAct,
    private val eventBus: EventBus,
) : ViewModel() {

    private val _baseCurrency = MutableStateFlow("")
    val baseCurrency = _baseCurrency.asStateFlow()

    private val _loan = MutableStateFlow<Loan?>(null)
    val loan = _loan.asStateFlow()

    private val _displayLoanRecords =
        MutableStateFlow<ImmutableList<DisplayLoanRecord>>(persistentListOf())
    val displayLoanRecords = _displayLoanRecords.asStateFlow()

    private val _amountPaid = MutableStateFlow(0.0)
    val amountPaid = _amountPaid.asStateFlow()

    private val _accounts = MutableStateFlow<ImmutableList<Account>>(persistentListOf())
    val accounts = _accounts.asStateFlow()

    private val _loanInterestAmountPaid = MutableStateFlow(0.0)
    val loanAmountPaid = _loanInterestAmountPaid.asStateFlow()

    private val _selectedLoanAccount = MutableStateFlow<Account?>(null)
    val selectedLoanAccount = _selectedLoanAccount.asStateFlow()

    private var associatedTransaction: Transaction? = null

    private val _createLoanTransaction = MutableStateFlow(false)
    val createLoanTransaction = _createLoanTransaction.asStateFlow()

    private var defaultCurrencyCode = ""

    fun start(screen: LoanDetailsScreen) {
        load(loanId = screen.loanId)
    }

    private fun load(loanId: UUID) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                _baseCurrency.value = it
            }

            _accounts.value = accountsAct(Unit)

            _loan.value = loanByIdAct(loanId)

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
                        )

                        DisplayLoanRecord(
                            it.toDomain(),
                            account = account,
                            loanRecordTransaction = trans != null,
                            loanRecordCurrencyCode = account?.currency ?: defaultCurrencyCode,
                            loanCurrencyCode = selectedLoanAccount.value?.currency
                                ?: defaultCurrencyCode
                        )
                    }.toImmutableList()
            }

            computationThread {
                // Using a local variable to calculate the amount and then reassigning to
                // the State variable to reduce the amount of compose re-draws
                var amtPaid = 0.0
                var loanInterestAmtPaid = 0.0
                displayLoanRecords.value.forEach {
                    val convertedAmount = it.loanRecord.convertedAmount ?: it.loanRecord.amount
                    if (!it.loanRecord.interest) {
                        amtPaid += convertedAmount
                    } else {
                        loanInterestAmtPaid += convertedAmount
                    }
                }

                _amountPaid.value = amtPaid
                _loanInterestAmountPaid.value = loanInterestAmtPaid
            }

            associatedTransaction = ioThread {
                transactionDao.findLoanTransaction(loanId = loan.value!!.id)?.toDomain()
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

            _loan.value?.let {
                loanTransactionsLogic.Loan.recalculateLoanRecords(
                    oldLoanAccountId = it.accountId,
                    newLoanAccountId = loan.accountId,
                    loanId = loan.id
                )
            }

            loanTransactionsLogic.Loan.editAssociatedLoanTransaction(
                loan = loan,
                createLoanTransaction = createLoanTransaction,
                transaction = associatedTransaction
            )

            loanCreator.edit(loan) {
                load(loanId = it.id)
            }

            TestIdlingResource.decrement()
        }
    }

    fun deleteLoan() {
        val loan = loan.value ?: return

        viewModelScope.launch {
            TestIdlingResource.increment()

            loanTransactionsLogic.Loan.deleteAssociatedLoanTransactions(loan.id)

            loanCreator.delete(loan) {
                // close screen
                nav.back()
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

            val modifiedData = data.copy(
                convertedAmount = loanTransactionsLogic.LoanRecord.calculateConvertedAmount(
                    data = data,
                    loanAccountId = localLoan.accountId
                )
            )

            val loanRecordUUID = loanRecordCreator.create(
                loanId = loanId,
                data = modifiedData
            ) {
                load(loanId = loanId)
            }

            loanRecordUUID?.let {
                loanTransactionsLogic.LoanRecord.createAssociatedLoanRecordTransaction(
                    data = modifiedData,
                    loan = localLoan,
                    loanRecordId = it
                )
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoanRecord(editLoanRecordData: EditLoanRecordData) {
        viewModelScope.launch {
            val loanRecord = editLoanRecordData.newLoanRecord
            TestIdlingResource.increment()

            val localLoan: Loan = _loan.value ?: return@launch

            val convertedAmount =
                loanTransactionsLogic.LoanRecord.calculateConvertedAmount(
                    loanAccountId = localLoan.accountId,
                    newLoanRecord = editLoanRecordData.newLoanRecord,
                    oldLoanRecord = editLoanRecordData.originalLoanRecord,
                    reCalculateLoanAmount = editLoanRecordData.reCalculateLoanAmount
                )

            val modifiedLoanRecord =
                editLoanRecordData.newLoanRecord.copy(convertedAmount = convertedAmount)

            loanTransactionsLogic.LoanRecord.editAssociatedLoanRecordTransaction(
                loan = localLoan,
                createLoanRecordTransaction = editLoanRecordData.createLoanRecordTransaction,
                loanRecord = loanRecord,
            )

            loanRecordCreator.edit(modifiedLoanRecord) {
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

            loanTransactionsLogic.LoanRecord.deleteAssociatedLoanRecordTransaction(loanRecordId = loanRecord.id)

            TestIdlingResource.decrement()
        }
    }

    fun onLoanTransactionChecked(boolean: Boolean) {
        _createLoanTransaction.value = boolean
    }

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                eventBus.post(AccountUpdatedEvent)
                _accounts.value = accountsAct(Unit)
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
}
