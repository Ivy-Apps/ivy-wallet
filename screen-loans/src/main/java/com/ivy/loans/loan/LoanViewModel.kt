package com.ivy.loans.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Loan
import com.ivy.domain.event.AccountUpdatedEvent
import com.ivy.domain.event.EventBus
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.legacy.utils.format
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.ioThread
import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.model.LoanType
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

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
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _loans = MutableStateFlow<ImmutableList<DisplayLoan>>(persistentListOf())
    val loans = _loans.asStateFlow()

    private val _accounts = MutableStateFlow<ImmutableList<Account>>(persistentListOf())
    val accounts = _accounts.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private var defaultCurrencyCode = ""

    private val _state = MutableStateFlow(LoanScreenState())
    val state: StateFlow<LoanScreenState> = _state

    fun start() {
        viewModelScope.launch(Dispatchers.Default) {
            TestIdlingResource.increment()

            defaultCurrencyCode = ioThread {
                settingsDao.findFirst().currency
            }.also {
                _baseCurrencyCode.value = it
            }

            initialiseAccounts()

            var totalOweAmount = 0.0
            var totalOwedAmount = 0.0
            var currCode = ""

            _loans.value = ioThread {
                loansAct(Unit)
                    .map { loan ->
                        val amountPaid = calculateAmountPaid(loan)
                        val loanAmount = loan.amount
                        val percentPaid = amountPaid / loanAmount
                        currCode = findCurrencyCode(accounts.value, loan.accountId)

                        when (loan.type) {
                            LoanType.BORROW -> totalOweAmount += (loanAmount - amountPaid)
                            LoanType.LEND -> totalOwedAmount += (loanAmount - amountPaid)
                        }

                        DisplayLoan(
                            loan = loan,
                            amountPaid = amountPaid,
                            currencyCode = currCode,
                            formattedDisplayText = "${amountPaid.format(currCode)} $currCode / ${
                                loanAmount.format(
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
            _state.value = LoanScreenState(
                baseCurrency = defaultCurrencyCode,
                loans = _loans.value,
                accounts = accounts.value,
                selectedAccount = selectedAccount.value,
                totalOweAmount = getTotalOweAmount(totalOweAmount, currCode),
                totalOwedAmount = getTotalOwedAmount(totalOwedAmount, currCode)
            )

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
        val accounts = accountsAct(Unit)
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

            uuid?.let {
                loanTransactionsLogic.Loan.createAssociatedLoanTransaction(data = data, loanId = it)
            }

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<DisplayLoan>) {
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

    fun createAccount(data: CreateAccountData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            accountCreator.createAccount(data) {
                eventBus.post(AccountUpdatedEvent)
                _accounts.value = accountsAct(Unit)
                _state.value = state.value.copy(accounts = _accounts.value)
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

    fun onEvent(event: LoanScreenEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is LoanScreenEvent.OnLoanCreate -> {
                    createLoan(event.createLoanData)
                }

                is LoanScreenEvent.OnAddLoan -> {
                    _state.value = _state.value.copy(
                        loanModalData = LoanModalData(
                            loan = null,
                            baseCurrency = baseCurrencyCode.value,
                            selectedAccount = selectedAccount.value
                        )
                    )
                }

                is LoanScreenEvent.OnLoanModalDismiss -> {
                    _state.value = _state.value.copy(
                        loanModalData = null
                    )
                }

                is LoanScreenEvent.OnReOrderModalShow -> {
                    _state.value = _state.value.copy(
                        reorderModalVisible = event.show
                    )
                }

                is LoanScreenEvent.OnReordered -> {
                    reorder(event.reorderedList)
                    _state.value = _state.value.copy(
                        loans = event.reorderedList
                    )
                }

                is LoanScreenEvent.OnCreateAccount -> {
                    createAccount(event.accountData)
                }
            }
        }
    }
}

data class LoanScreenState(
    val baseCurrency: String = "",
    val loans: List<DisplayLoan> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val loanModalData: LoanModalData? = null,
    val reorderModalVisible: Boolean = false,
    val totalOweAmount: String = "",
    val totalOwedAmount: String = ""
)

sealed class LoanScreenEvent {
    data class OnLoanCreate(val createLoanData: CreateLoanData) : LoanScreenEvent()
    data class OnReordered(val reorderedList: List<DisplayLoan>) : LoanScreenEvent()
    data class OnCreateAccount(val accountData: CreateAccountData) : LoanScreenEvent()
    data class OnReOrderModalShow(val show: Boolean) : LoanScreenEvent()
    object OnAddLoan : LoanScreenEvent()
    object OnLoanModalDismiss : LoanScreenEvent()
}
