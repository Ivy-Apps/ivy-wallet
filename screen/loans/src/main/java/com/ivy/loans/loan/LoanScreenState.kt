package com.ivy.loans.loan

import com.ivy.legacy.datamodel.Account
import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.wallet.ui.theme.modal.LoanModalData
import kotlinx.collections.immutable.ImmutableList
import java.time.Instant

data class LoanScreenState(
    val baseCurrency: String,
    val completedLoans: ImmutableList<DisplayLoan>,
    val pendingLoans: ImmutableList<DisplayLoan>,
    val accounts: ImmutableList<Account>,
    val selectedAccount: Account?,
    val loanModalData: LoanModalData?,
    val reorderModalVisible: Boolean,
    val totalOweAmount: String,
    val totalOwedAmount: String,
    val paidOffLoanVisibility: Boolean,
    val dateTime: Instant,
    val selectedTab: LoanTab
)