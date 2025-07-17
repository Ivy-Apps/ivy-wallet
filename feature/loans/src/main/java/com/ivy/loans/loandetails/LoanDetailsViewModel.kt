package com.ivy.loans.loandetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.Transaction
import com.ivy.base.model.LoanRecordType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.legacy.datamodel.temp.toLegacy
import com.ivy.legacy.datamodel.temp.toLegacyDomain
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.computationThread
import com.ivy.legacy.utils.ioThread
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.loans.loandetails.events.DeleteLoanModalEvent
import com.ivy.loans.loandetails.events.LoanDetailsScreenEvent
import com.ivy.loans.loandetails.events.LoanModalEvent
import com.ivy.loans.loandetails.events.LoanRecordModalEvent
import com.ivy.navigation.LoanDetailsScreen
import com.ivy.navigation.Navigation
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.time.impl.DateTimePicker
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.loan.LoanByIdAct
import com.ivy.wallet.domain.deprecated.logic.LoanCreator
import com.ivy.wallet.domain.deprecated.logic.LoanRecordCreator
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData
import com.ivy.wallet.ui.theme.modal.LoanModalData
import com.ivy.wallet.ui.theme.modal.LoanRecordModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val loanRecordDao: LoanRecordDao,
    private val loanCreator: LoanCreator,
    private val loanRecordCreator: LoanRecordCreator,
    private val settingsDao: SettingsDao,
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val accountCreator: AccountCreator,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val nav: Navigation,
    private val accountsAct: AccountsAct,
    private val loanByIdAct: LoanByIdAct,
    private val timeConverter: TimeConverter,
    private val timeProvider: TimeProvider,
    private val dateTimePicker: DateTimePicker,
) : ComposeViewModel<LoanDetailsScreenState, LoanDetailsScreenEvent>() {

    private val baseCurrency = mutableStateOf("")
    private val loan = mutableStateOf<Loan?>(null)
    private val displayLoanRecords =
        mutableStateOf<ImmutableList<DisplayLoanRecord>>(persistentListOf())
    private val loanTotalAmount = mutableDoubleStateOf(0.0)
    private val amountPaid = mutableDoubleStateOf(0.0)
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val loanInterestAmountPaid = mutableDoubleStateOf(0.0)
    private val selectedLoanAccount = mutableStateOf<Account?>(null)
    private var associatedTransaction: Transaction? = null
    private val createLoanTransaction = mutableStateOf(false)
    private var defaultCurrencyCode = ""
    private val loanModalData = mutableStateOf<LoanModalData?>(null)
    private val loanRecordModalData = mutableStateOf<LoanRecordModalData?>(null)
    private val waitModalVisible = mutableStateOf(false)
    private val isDeleteModalVisible = mutableStateOf(false)
    private var dateTime = mutableStateOf<Instant>(timeProvider.utcNow())
    lateinit var screen: LoanDetailsScreen

    @Composable
    override fun uiState(): LoanDetailsScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return LoanDetailsScreenState(
            baseCurrency = baseCurrency.value,
            loan = loan.value,
            displayLoanRecords = displayLoanRecords.value,
            loanTotalAmount = loanTotalAmount.doubleValue,
            amountPaid = amountPaid.doubleValue,
            loanAmountPaid = loanInterestAmountPaid.doubleValue,
            accounts = accounts.value,
            selectedLoanAccount = selectedLoanAccount.value,
            createLoanTransaction = createLoanTransaction.value,
            loanModalData = loanModalData.value,
            loanRecordModalData = loanRecordModalData.value,
            waitModalVisible = waitModalVisible.value,
            isDeleteModalVisible = isDeleteModalVisible.value,
            dateTime = dateTime.value
        )
    }

    override fun onEvent(event: LoanDetailsScreenEvent) {
        when (event) {
            is LoanRecordModalEvent -> handleLoanRecordModalEvents(event)
            is LoanModalEvent -> handleLoanModalEvents(event)
            is DeleteLoanModalEvent -> handleDeleteLoanModalEvents(event)
            is LoanDetailsScreenEvent -> handleLoanDetailsScreenEvents(event)
        }
    }

    private fun handleLoanRecordModalEvents(event: LoanDetailsScreenEvent) {
        when (event) {
            is LoanRecordModalEvent.OnClickLoanRecord -> {
                loanRecordModalData.value = LoanRecordModalData(
                    loanRecord = event.displayLoanRecord.loanRecord,
                    baseCurrency = event.displayLoanRecord.loanRecordCurrencyCode,
                    selectedAccount = event.displayLoanRecord.account,
                    createLoanRecordTransaction = event.displayLoanRecord.loanRecordTransaction,
                    isLoanInterest = event.displayLoanRecord.loanRecord.interest,
                    loanAccountCurrencyCode = event.displayLoanRecord.loanCurrencyCode
                )
            }

            is LoanRecordModalEvent.OnCreateLoanRecord -> {
                createLoanRecord(event.loanRecordData)
            }

            is LoanRecordModalEvent.OnDeleteLoanRecord -> {
                deleteLoanRecord(event.loanRecord)
            }

            LoanRecordModalEvent.OnDismissLoanRecord -> {
                loanRecordModalData.value = null
                dateTime.value = timeProvider.utcNow()
            }

            is LoanRecordModalEvent.OnEditLoanRecord -> {
                editLoanRecord(event.loanRecordData)
            }

            is LoanRecordModalEvent.OnChangeDate -> {
                handleChangeDate()
            }
            is LoanRecordModalEvent.OnChangeTime -> {
                handleChangeTime()
            }
            else -> {}
        }
    }

    private fun handleLoanModalEvents(event: LoanDetailsScreenEvent) {
        when (event) {
            LoanModalEvent.OnDismissLoanModal -> {
                loanModalData.value = null
                dateTime.value = timeProvider.utcNow()
            }

            is LoanModalEvent.OnEditLoanModal -> {
                editLoan(event.loan, event.createLoanTransaction)
            }

            LoanModalEvent.PerformCalculation -> {
                waitModalVisible.value = true
            }

            LoanModalEvent.OnChangeDate -> {
                handleLoanChangeDate()
            }

            LoanModalEvent.OnChangeTime -> {
                handleLoanChangeTime()
            }

            else -> {}
        }
    }

    private fun handleDeleteLoanModalEvents(event: LoanDetailsScreenEvent) {
        when (event) {
            DeleteLoanModalEvent.OnDeleteLoan -> {
                deleteLoan()
                isDeleteModalVisible.value = false
            }

            is DeleteLoanModalEvent.OnDismissDeleteLoan -> {
                isDeleteModalVisible.value = event.isDeleteModalVisible
            }

            else -> {}
        }
    }

    private fun handleLoanDetailsScreenEvents(event: LoanDetailsScreenEvent) {
        when (event) {
            LoanDetailsScreenEvent.OnAmountClick -> {
                loanModalData.value = LoanModalData(
                    loan = loan.value,
                    baseCurrency = baseCurrency.value,
                    autoFocusKeyboard = false,
                    autoOpenAmountModal = true,
                    selectedAccount = selectedLoanAccount.value,
                    createLoanTransaction = createLoanTransaction.value
                )
            }

            LoanDetailsScreenEvent.OnEditLoanClick -> {
                loanModalData.value = LoanModalData(
                    loan = loan.value,
                    baseCurrency = baseCurrency.value,
                    autoFocusKeyboard = false,
                    selectedAccount = selectedLoanAccount.value,
                    createLoanTransaction = createLoanTransaction.value
                )
            }

            LoanDetailsScreenEvent.OnAddRecord -> {
                loanRecordModalData.value = LoanRecordModalData(
                    loanRecord = null,
                    baseCurrency = baseCurrency.value,
                    selectedAccount = selectedLoanAccount.value
                )
            }

            is LoanDetailsScreenEvent.OnCreateAccount -> {
                createAccount(event.data)
            }

            else -> {}
        }
    }

    private fun start() {
        load(loanId = screen.loanId)
    }

    private fun load(loanId: UUID) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            dateTime.value = timeProvider.utcNow()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                baseCurrency.value = it
            }

            accounts.value = accountsAct(Unit)

            loan.value = loanByIdAct(loanId)

            loan.value?.let { loan ->
                selectedLoanAccount.value = accounts.value.find {
                    loan.accountId == it.id
                }

                selectedLoanAccount.value?.let { acc ->
                    baseCurrency.value = acc.currency ?: defaultCurrencyCode
                }
            }

            computationThread {
                displayLoanRecords.value =
                    ioThread { loanRecordDao.findAllByLoanId(loanId = loanId) }.map {
                        val trans = ioThread {
                            transactionRepository.findLoanRecordTransaction(
                                it.id
                            )
                        }

                        val account = findAccount(
                            accounts = accounts.value,
                            accountId = it.accountId,
                        )

                        DisplayLoanRecord(
                            it.toLegacyDomain(),
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
                    // We do not want to calculate records that increase loan.
                    if (it.loanRecord.loanRecordType == LoanRecordType.INCREASE) {
                        return@forEach
                    }
                    val convertedAmount = it.loanRecord.convertedAmount ?: it.loanRecord.amount
                    if (!it.loanRecord.interest) {
                        amtPaid += convertedAmount
                    } else {
                        loanInterestAmtPaid += convertedAmount
                    }
                }

                amountPaid.doubleValue = amtPaid
                loanInterestAmountPaid.doubleValue = loanInterestAmtPaid
            }

            computationThread {
                // Calculate total amount of loan borrowed or lent.
                // That is initial amount + each record that increased the loan.
                val totalAmount =
                    displayLoanRecords.value.fold(loan.value?.amount ?: 0.0) { value, record ->
                        if (record.loanRecord.loanRecordType == LoanRecordType.INCREASE) {
                            val convertedAmount =
                                record.loanRecord.convertedAmount ?: record.loanRecord.amount
                            value + convertedAmount
                        } else {
                            value
                        }
                    }
                loanTotalAmount.doubleValue = totalAmount
            }

            associatedTransaction = ioThread {
                transactionRepository.findLoanTransaction(loanId = loan.value!!.id).let {
                    it?.toLegacy(transactionMapper)
                }
            }

            associatedTransaction?.let {
                createLoanTransaction.value = true
            } ?: run {
                createLoanTransaction.value = false
            }

            TestIdlingResource.decrement()
        }
    }

    fun editLoan(loan: Loan, createLoanTransaction: Boolean = false) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            this@LoanDetailsViewModel.loan.value?.let {
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

    private fun deleteLoan() {
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

    private fun createLoanRecord(data: CreateLoanRecordData) {
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

    private fun editLoanRecord(editLoanRecordData: EditLoanRecordData) {
        viewModelScope.launch {
            val loanRecord = editLoanRecordData.newLoanRecord
            TestIdlingResource.increment()

            val localLoan: Loan = loan.value ?: return@launch

            val convertedAmount = loanTransactionsLogic.LoanRecord.calculateConvertedAmount(
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

    private fun deleteLoanRecord(loanRecord: LoanRecord) {
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

    private fun handleChangeDate() {
        dateTimePicker.pickDate(
            initialDate = loanRecordModalData.value?.loanRecord?.dateTime?.let {
                with(timeConverter) { it.toLocalDateTime().toUTC() }
            } ?: timeProvider.utcNow()
        ) { localDate ->

            val localTime = loanRecordModalData.value?.loanRecord?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()

            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun handleChangeTime() {
        dateTimePicker.pickTime(
            initialTime = loanRecordModalData.value?.loanRecord?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()
        ) { localTime ->
            val localDate = loanRecordModalData.value?.loanRecord?.dateTime?.let {
                with(timeConverter) { it.toLocalDate() }
            } ?: timeProvider.localDateNow()

            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun updateDateTime(newDateTime: LocalDateTime) {
        val newDateTimeUtc = with(timeConverter) { newDateTime.toUTC() }
        loanRecordModalData.value?.let { currentData ->
            loanRecordModalData.value = currentData.copy(
                loanRecord = currentData.loanRecord?.copy(
                    dateTime = newDateTimeUtc
                )
            )
            dateTime.value = newDateTimeUtc
        }
    }

    private fun handleLoanChangeDate() {
        dateTimePicker.pickDate(
            initialDate = loanModalData.value?.loan?.dateTime?.let {
                with(timeConverter) { it.toUTC() }
            } ?: timeProvider.utcNow()
        ) { localDate ->

            val localTime = loanModalData.value?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()

            updateLoanDateTime(localDate.atTime(localTime))
        }
    }

    private fun handleLoanChangeTime() {
        dateTimePicker.pickTime(
            initialTime = loanModalData.value?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()
        ) { localTime ->
            val localDate = loanModalData.value?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalDate() }
            } ?: timeProvider.localDateNow()

            updateLoanDateTime(localDate.atTime(localTime))
        }
    }

    private fun updateLoanDateTime(newDateTime: LocalDateTime) {
        val newDateTimeUtc = with(timeConverter) { newDateTime.toUTC() }
        loanModalData.value?.let { currentData ->
            loanModalData.value = currentData.copy(
                loan = currentData.loan?.copy(
                    dateTime = newDateTime
                )
            )
            dateTime.value = newDateTimeUtc
        }
    }

    fun onLoanTransactionChecked(boolean: Boolean) {
        createLoanTransaction.value = boolean
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                accounts.value = accountsAct(Unit)
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
