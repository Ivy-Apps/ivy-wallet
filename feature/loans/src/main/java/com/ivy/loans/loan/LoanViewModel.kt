package com.ivy.loans.loan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.model.processByType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.model.LoanType
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.ioThread
import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.time.impl.DateTimePicker
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.loan.LoansAct
import com.ivy.wallet.domain.deprecated.logic.LoanCreator
import com.ivy.wallet.domain.deprecated.logic.loantrasactions.LoanTransactionsLogic
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanData
import com.ivy.wallet.ui.theme.modal.LoanModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRecordDao: LoanRecordDao,
    private val settingsDao: SettingsDao,
    private val loanCreator: LoanCreator,
    private val sharedPrefs: SharedPrefs,
    private val accountCreator: AccountCreator,
    private val loanTransactionsLogic: LoanTransactionsLogic,
    private val loansAct: LoansAct,
    private val accountsAct: AccountsAct,
    private val loanWriter: WriteLoanDao,
    private val timeConverter: TimeConverter,
    private val timeProvider: TimeProvider,
    private val dateTimePicker: DateTimePicker
) : ComposeViewModel<LoanScreenState, LoanScreenEvent>() {

    private var baseCurrencyCode by mutableStateOf(getDefaultFIATCurrency().currencyCode)
    private var completedLoans by mutableStateOf<ImmutableList<DisplayLoan>>(persistentListOf())
    private var pendingLoans by mutableStateOf<ImmutableList<DisplayLoan>>(persistentListOf())
    private var accounts by mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private var selectedAccount by mutableStateOf<Account?>(null)
    private var loanModalData by mutableStateOf<LoanModalData?>(null)
    private var reorderModalVisible by mutableStateOf(false)
    private var dateTime by mutableStateOf<Instant>(timeProvider.utcNow())
    private var selectedTab by mutableStateOf(LoanTab.PENDING)

    /** If true paid off loans will be visible */
    private var paidOffLoanVisibility by mutableStateOf(true)

    /** Contains all loans including both paidOff and pending*/
    private var allLoans: ImmutableList<DisplayLoan> = persistentListOf()
    private var defaultCurrencyCode = ""
    private var totalOweAmount = 0.0
    private var totalOwedAmount = 0.0

    @Composable
    override fun uiState(): LoanScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return LoanScreenState(
            baseCurrency = getBaseCurrencyCode(),
            accounts = getAccounts(),
            selectedAccount = getSelectedAccount(),
            loanModalData = getLoanModalData(),
            reorderModalVisible = getReorderModalVisible(),
            totalOweAmount = getTotalOweAmount(totalOweAmount, defaultCurrencyCode),
            totalOwedAmount = getTotalOwedAmount(totalOwedAmount, defaultCurrencyCode),
            paidOffLoanVisibility = getPaidOffLoanVisibility(),
            dateTime = dateTime,
            selectedTab = getSelectedTab(),
            completedLoans = getCompletedLoans(),
            pendingLoans = getPendingLoans()
        )
    }

    fun setTab(tab: LoanTab) {
        selectedTab = tab
    }

    @Composable
    private fun getSelectedTab(): LoanTab {
        return selectedTab
    }

    @Composable
    private fun getCompletedLoans(): ImmutableList<DisplayLoan> {
        return completedLoans
    }

    @Composable
    private fun getPendingLoans(): ImmutableList<DisplayLoan> {
        return pendingLoans
    }

    @Composable
    private fun getReorderModalVisible() = reorderModalVisible

    @Composable
    private fun getLoanModalData() = loanModalData

    @Composable
    private fun getBaseCurrencyCode(): String {
        return baseCurrencyCode
    }

    @Composable
    private fun getSelectedAccount() = selectedAccount

    @Composable
    private fun getAccounts() = accounts

    @Composable
    private fun getPaidOffLoanVisibility(): Boolean = paidOffLoanVisibility

    override fun onEvent(event: LoanScreenEvent) {
        when (event) {
            is LoanScreenEvent.OnLoanCreate -> {
                createLoan(event.createLoanData)
            }

            is LoanScreenEvent.OnAddLoan -> {
                loanModalData = LoanModalData(
                    loan = null,
                    baseCurrency = baseCurrencyCode,
                    selectedAccount = selectedAccount
                )
            }

            is LoanScreenEvent.OnLoanModalDismiss -> {
                loanModalData = null
                dateTime = timeProvider.utcNow()
            }

            is LoanScreenEvent.OnReOrderModalShow -> {
                reorderModalVisible = event.show
            }

            is LoanScreenEvent.OnReordered -> {
                reorder(event.reorderedList)
            }

            is LoanScreenEvent.OnCreateAccount -> {
                createAccount(event.accountData)
            }

            LoanScreenEvent.OnTogglePaidOffLoanVisibility -> {
                updatePaidOffLoanVisibility()
            }

            is LoanScreenEvent.OnChangeDate -> {
                handleChangeDate()
            }

            is LoanScreenEvent.OnChangeTime -> {
                handleChangeTime()
            }

            is LoanScreenEvent.OnTabChanged -> {
                setTab(event.tab)
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.Default) {
            TestIdlingResource.increment()

            dateTime = timeProvider.utcNow()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                baseCurrencyCode = it
            }

            initialiseAccounts()

            totalOweAmount = 0.0
            totalOwedAmount = 0.0

            allLoans = ioThread {
                loansAct(Unit)
                    .map { loan ->
                        val (amountPaid, loanTotalAmount) = calculateAmountPaidAndTotalAmount(loan)
                        val percentPaid = if (loanTotalAmount != 0.0) {
                            amountPaid / loanTotalAmount
                        } else {
                            0.0
                        }
                        var currCode = findCurrencyCode(accounts, loan.accountId)

                        when (loan.type) {
                            LoanType.BORROW -> totalOweAmount += (loanTotalAmount - amountPaid)
                            LoanType.LEND -> totalOwedAmount += (loanTotalAmount - amountPaid)
                        }

                        DisplayLoan(
                            loan = loan,
                            loanTotalAmount = loanTotalAmount,
                            amountPaid = amountPaid,
                            currencyCode = currCode,
                            formattedDisplayText = "${amountPaid.format(currCode)} $currCode / ${
                                loanTotalAmount.format(
                                    currCode
                                )
                            } $currCode (${
                                percentPaid.times(
                                    100
                                ).format(2)
                            }%)",
                            percentPaid = percentPaid
                        )
                    }.toImmutableList()
            }
            loadPendingLoans()
            loadCompletedLoans()

            TestIdlingResource.decrement()
        }
    }

    private fun getTotalOwedAmount(totalOwedAmount: Double, currCode: String): String {
        return if (totalOwedAmount != 0.0) {
            "${totalOwedAmount.format(currCode)} $currCode"
        } else {
            ""
        }
    }

    private fun getTotalOweAmount(totalOweAmount: Double, currCode: String): String {
        return if (totalOweAmount != 0.0) {
            "${totalOweAmount.format(currCode)} $currCode"
        } else {
            ""
        }
    }

    private suspend fun initialiseAccounts() {
        val accountsList = accountsAct(Unit)
        accounts = accountsList
        selectedAccount = defaultAccountId(accountsList)
        selectedAccount?.let {
            baseCurrencyCode = it.currency ?: defaultCurrencyCode
        }
    }

    private fun handleChangeDate() {
        dateTimePicker.pickDate(
            initialDate = loanModalData?.loan?.dateTime?.let {
                with(timeConverter) { it.toUTC() }
            } ?: timeProvider.utcNow()
        ) { localDate ->
            val localTime = loanModalData?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()

            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun handleChangeTime() {
        dateTimePicker.pickTime(
            initialTime = loanModalData?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalTime() }
            } ?: timeProvider.localTimeNow()
        ) { localTime ->
            val localDate = loanModalData?.loan?.dateTime?.let {
                with(timeConverter) { it.toLocalDate() }
            } ?: timeProvider.localDateNow()

            updateDateTime(localDate.atTime(localTime))
        }
    }

    private fun updateDateTime(newDateTime: LocalDateTime) {
        val newDateTimeUtc = with(timeConverter) { newDateTime.toUTC() }
        loanModalData?.let { currentData ->
            loanModalData = currentData.copy(
                loan = currentData.loan?.copy(
                    dateTime = newDateTime
                )
            )
            dateTime = newDateTimeUtc
        }
    }

    private fun createLoan(data: CreateLoanData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val uuid = loanCreator.create(data) {
                start()
            }

            uuid?.let {
                loanTransactionsLogic.Loan.createAssociatedLoanTransaction(data = data, loanId = it)
            }

            TestIdlingResource.decrement()
        }
    }

    private fun reorder(newOrder: List<DisplayLoan>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.forEachIndexed { index, item ->
                    loanWriter.save(
                        item.loan.toEntity().copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            TestIdlingResource.decrement()
        }
    }

    private fun loadCompletedLoans() {
        completedLoans = allLoans.filter { loan -> loan.percentPaid >= 1.0 }.toImmutableList()
    }

    private fun loadPendingLoans() {
        pendingLoans = allLoans.filter { loan -> loan.percentPaid < 1.0 }.toImmutableList()
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                accounts = accountsAct(Unit)
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

    /**
     *  Calculates the total amount paid and the total loan amount including any changes made to the loan.
     *  @return A Pair containing the total amount paid and the total loan amount.
     */
    private suspend fun calculateAmountPaidAndTotalAmount(loan: Loan): Pair<Double, Double> {
        val loanRecords = ioThread { loanRecordDao.findAllByLoanId(loanId = loan.id) }
        val (amountPaid, loanTotalAmount) = loanRecords.fold(0.0 to loan.amount) { value, loanRecord ->
            val (currentAmountPaid, currentLoanTotalAmount) = value
            if (loanRecord.interest) return@fold value
            val convertedAmount = loanRecord.convertedAmount ?: loanRecord.amount

            loanRecord.loanRecordType.processByType(
                decreaseAction = { currentAmountPaid + convertedAmount to currentLoanTotalAmount },
                increaseAction = { currentAmountPaid to currentLoanTotalAmount + convertedAmount }
            )
        }
        return amountPaid to loanTotalAmount
    }

    private fun updatePaidOffLoanVisibility() {
        paidOffLoanVisibility = paidOffLoanVisibility.not()
    }
}
