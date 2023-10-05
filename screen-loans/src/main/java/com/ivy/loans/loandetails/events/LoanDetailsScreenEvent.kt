package com.ivy.loans.loandetails.events

import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData

sealed class LoanDetailsScreenEvent {
    data object OnEditLoanClick : LoanDetailsScreenEvent()
    data object OnAmountClick : LoanDetailsScreenEvent()
    data object OnAddRecord : LoanDetailsScreenEvent()
    data class OnCreateAccount(val data: CreateAccountData) : LoanModalEvent()
}
