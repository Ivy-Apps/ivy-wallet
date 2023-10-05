package com.ivy.loans.loandetails.events

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData

sealed class LoanRecordModalState: LoanDetailsScreenEvent() {
    data class OnClickLoanRecord(val displayLoanRecord: DisplayLoanRecord) : LoanRecordModalState()
    data class OnCreateLoanRecord(val loanRecordData: CreateLoanRecordData) :
        LoanRecordModalState()
    data class OnDeleteLoanRecord(val loanRecord: LoanRecord) : LoanRecordModalState()
    data class OnEditLoanRecord(val loanRecordData: EditLoanRecordData) : LoanRecordModalState()
    data object OnDismissLoanRecord : LoanRecordModalState()
}
