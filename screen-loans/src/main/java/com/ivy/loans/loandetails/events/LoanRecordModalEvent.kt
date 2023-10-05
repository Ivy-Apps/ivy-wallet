package com.ivy.loans.loandetails.events

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData

sealed interface LoanRecordModalEvent : LoanDetailsScreenEvent {
    data class OnClickLoanRecord(val displayLoanRecord: DisplayLoanRecord) : LoanRecordModalEvent
    data class OnCreateLoanRecord(val loanRecordData: CreateLoanRecordData) :
        LoanRecordModalEvent

    data class OnDeleteLoanRecord(val loanRecord: LoanRecord) : LoanRecordModalEvent
    data class OnEditLoanRecord(val loanRecordData: EditLoanRecordData) : LoanRecordModalEvent
    data object OnDismissLoanRecord : LoanRecordModalEvent
}
