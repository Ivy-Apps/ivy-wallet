package com.ivy.loans.loan

import com.ivy.legacy.datamodel.Account
import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.wallet.ui.theme.modal.LoanModalData

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