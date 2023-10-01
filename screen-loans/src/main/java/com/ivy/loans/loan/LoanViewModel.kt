package com.ivy.loans.loan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Loan
import com.ivy.core.datamodel.LoanType
import com.ivy.core.db.read.LoanRecordDao
import com.ivy.core.db.read.SettingsDao
import com.ivy.core.db.write.LoanWriter
import com.ivy.core.event.AccountUpdatedEvent
import com.ivy.core.event.EventBus
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.data.SharedPrefs
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
    private val loanWriter: LoanWriter,
) : ComposeViewModel<LoanScreenState, LoanScreenEvent>() {

    private val baseCurrencyCode = mutableStateOf(getDefaultFIATCurrency().currencyCode)
    private val loans = mutableStateOf<ImmutableList<DisplayLoan>>(persistentListOf())
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val selectedAccount = mutableStateOf<Account?>(null)
    private val loanModalData = mutableStateOf<LoanModalData?>(null)
    private val reorderModalVisible = mutableStateOf(false)
    private var defaultCurrencyCode = ""
    private var totalOweAmount = 0.0
    private var totalOwedAmount = 0.0

    @Composable
    override fun uiState(): LoanScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        return LoanScreenState(
            baseCurrency = defaultCurrencyCode,
            loans = getLoanValue(),
            accounts = getAccounts(),
            selectedAccount = getSelectedAccount(),
            loanModalData = getLoanModalData(),
            reorderModalVisible = reorderModalVisible.value,
            totalOweAmount = getTotalOweAmount(totalOweAmount, defaultCurrencyCode),
            totalOwedAmount = getTotalOwedAmount(totalOwedAmount, defaultCurrencyCode)
        )
    }

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
            loans.value = ioThread {
                loansAct(Unit)
                    .map { loan ->
                        val amountPaid = calculateAmountPaid(loan)
                        val loanAmount = loan.amount
                        val percentPaid = amountPaid / loanAmount
                        val currCode = findCurrencyCode(accounts.value, loan.accountId)

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
            TestIdlingResource.decrement()
        }
    }

    private suspend fun initialiseAccounts() {
        val accountList = accountsAct(Unit)
        accounts.value = accountList
        selectedAccount.value = defaultAccountId(accountList)
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

    @Composable
    private fun getLoanValue() = loans.value

    @Composable
    private fun getAccounts() = accounts.value

    @Composable
    private fun getLoanModalData() = loanModalData.value

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

    @Composable
    private fun getSelectedAccount() = selectedAccount.value
}