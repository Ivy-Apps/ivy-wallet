package com.ivy.loans.loan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.model.processByType
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.model.LoanType
import com.ivy.domain.ComposeViewModel
import com.ivy.domain.event.AccountUpdatedEvent
import com.ivy.domain.event.EventBus
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.ioThread
import com.ivy.loans.loan.data.DisplayLoan
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
    private val eventBus: EventBus,
    private val loanWriter: WriteLoanDao,
) : ComposeViewModel<LoanScreenState, LoanScreenEvent>() {

    private val baseCurrencyCode = mutableStateOf(getDefaultFIATCurrency().currencyCode)
    private val loans = mutableStateOf<ImmutableList<DisplayLoan>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val selectedAccount = mutableStateOf<Account?>(null)
    private val loanModalData = mutableStateOf<LoanModalData?>(null)
    private val reorderModalVisible = mutableStateOf(false)

    /** If true paid off loans will be visible */
    private val paidOffLoanVisibility = mutableStateOf(true)

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
            loans = getLoans(),
            accounts = getAccounts(),
            selectedAccount = getSelectedAccount(),
            loanModalData = getLoanModalData(),
            reorderModalVisible = getReorderModalVisible(),
            totalOweAmount = getTotalOweAmount(totalOweAmount, defaultCurrencyCode),
            totalOwedAmount = getTotalOwedAmount(totalOwedAmount, defaultCurrencyCode),
            paidOffLoanVisibility = getPaidOffLoanVisibility()
        )
    }

    @Composable
    private fun getReorderModalVisible() = reorderModalVisible.value

    @Composable
    private fun getLoanModalData() = loanModalData.value

    @Composable
    private fun getLoans(): ImmutableList<DisplayLoan> {
        return loans.value
    }

    @Composable
    private fun getBaseCurrencyCode(): String {
        return baseCurrencyCode.value
    }

    @Composable
    private fun getSelectedAccount() = selectedAccount.value

    @Composable
    private fun getAccounts() = accounts.value

    @Composable
    private fun getPaidOffLoanVisibility(): Boolean = paidOffLoanVisibility.value

    override fun onEvent(event: LoanScreenEvent) {
        when (event) {
            is LoanScreenEvent.OnLoanCreate -> {
                createLoan(event.createLoanData)
            }

            is LoanScreenEvent.OnAddLoan -> {
                loanModalData.value = LoanModalData(
                    loan = null,
                    baseCurrency = baseCurrencyCode.value,
                    selectedAccount = selectedAccount.value
                )
            }

            is LoanScreenEvent.OnLoanModalDismiss -> {
                loanModalData.value = null
            }

            is LoanScreenEvent.OnReOrderModalShow -> {
                reorderModalVisible.value = event.show
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
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.Default) {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                baseCurrencyCode.value = it
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
                        var currCode = findCurrencyCode(accounts.value, loan.accountId)

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
            filterLoans()

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
        accounts.value = accountsList
        selectedAccount.value = defaultAccountId(accountsList)
        selectedAccount.value?.let {
            baseCurrencyCode.value = it.currency ?: defaultCurrencyCode
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

    /** It filters [allLoans] and updates [loans] based on weather to show paid off loans or not */
    private fun filterLoans() {
        loans.value = when (paidOffLoanVisibility.value) {
            true -> allLoans
            false -> allLoans.filter { loan -> loan.percentPaid < 1.0 }.toImmutableList()
        }
    }

    private fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                eventBus.post(AccountUpdatedEvent)
                accounts.value = accountsAct(Unit)
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
        paidOffLoanVisibility.value = paidOffLoanVisibility.value.not()
        filterLoans()
    }
}
