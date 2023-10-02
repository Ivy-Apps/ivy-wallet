package com.ivy.loans.loandetails

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData

sealed class LoanDetailsScreenEvent {
    data object OnEditLoan : LoanDetailsScreenEvent()
    data object OnAmountClick : LoanDetailsScreenEvent()
    data object OnAddRecord : LoanDetailsScreenEvent()

    data class OnRecordClick(val displayLoanRecord: DisplayLoanRecord) : LoanDetailsScreenEvent()
    data class OnDeleteLoanRecord(val loanRecord: LoanRecord) : LoanDetailsScreenEvent()
    data class OnCreateLoanRecord(val loanRecordData: CreateLoanRecordData) :
        LoanDetailsScreenEvent()

    data class OnEditLoanRecord(val loanRecordData: EditLoanRecordData) : LoanDetailsScreenEvent()
    data object OnDeleteLoan : LoanDetailsScreenEvent()
    data class OnCreateAccount(val data: CreateAccountData) : LoanDetailsScreenEvent()
}
