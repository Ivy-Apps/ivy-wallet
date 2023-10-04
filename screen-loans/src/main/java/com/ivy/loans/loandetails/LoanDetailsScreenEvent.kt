package com.ivy.loans.loandetails

import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData

sealed class LoanDetailsScreenEvent {
    data object OnEditLoanClick : LoanDetailsScreenEvent()
    data object OnAmountClick : LoanDetailsScreenEvent()
    data object OnAddRecord : LoanDetailsScreenEvent()
    data class OnRecordClick(val displayLoanRecord: DisplayLoanRecord) : LoanDetailsScreenEvent()
    data class OnEditLoan(val loan: Loan, val createLoanTransaction: Boolean) : LoanDetailsScreenEvent()
    data class OnCreateLoanRecord(val loanRecordData: CreateLoanRecordData) :
        LoanDetailsScreenEvent()
    data class OnEditLoanRecord(val loanRecordData: EditLoanRecordData) : LoanDetailsScreenEvent()
    data class OnDeleteLoanRecord(val loanRecord: LoanRecord) : LoanDetailsScreenEvent()
    data object OnLoadRecordDismiss : LoanDetailsScreenEvent()
    data class OnCreateAccount(val data: CreateAccountData) : LoanDetailsScreenEvent()
    data object OnDismiss : LoanDetailsScreenEvent()
    data object PerformCalculation : LoanDetailsScreenEvent()
    data object OnDeleteLoan : LoanDetailsScreenEvent()
    data class OnDismissDeleteLoan(val isDeleteModalVisible: Boolean) : LoanDetailsScreenEvent()

}
