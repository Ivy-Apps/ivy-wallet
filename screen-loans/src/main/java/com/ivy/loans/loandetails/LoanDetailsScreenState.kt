package com.ivy.loans.loandetails

import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Loan
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.ui.theme.modal.LoanModalData
import com.ivy.wallet.ui.theme.modal.LoanRecordModalData

data class LoanDetailsScreenState(
    val baseCurrency: String = "",
    val loan: Loan? = null,
    val displayLoanRecords: List<DisplayLoanRecord> = emptyList(),
    val amountPaid: Double = 0.0,
    val loanAmountPaid: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val selectedLoanAccount: Account? = null,
    val createLoanTransaction: Boolean = false,
    val loanModalData: LoanModalData? = null,
    val loanRecordModalData: LoanRecordModalData? = null
)
