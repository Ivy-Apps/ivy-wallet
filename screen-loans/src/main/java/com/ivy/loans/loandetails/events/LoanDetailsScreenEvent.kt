package com.ivy.loans.loandetails.events

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.loans.loan.data.DisplayLoanRecord
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanRecordData
import com.ivy.wallet.domain.deprecated.logic.model.EditLoanRecordData

sealed class LoanDetailsScreenEvent {
    data object OnEditLoanClick : LoanDetailsScreenEvent()
    data object OnAmountClick : LoanDetailsScreenEvent()
    data object OnAddRecord : LoanDetailsScreenEvent()
    data class OnCreateAccount(val data: CreateAccountData) : LoanModalState()
}