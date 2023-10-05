package com.ivy.loans.loan

import com.ivy.loans.loan.data.DisplayLoan
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateLoanData

sealed interface LoanScreenEvent {
    data class OnLoanCreate(val createLoanData: CreateLoanData) : LoanScreenEvent
    data class OnReordered(val reorderedList: List<DisplayLoan>) : LoanScreenEvent
    data class OnCreateAccount(val accountData: CreateAccountData) : LoanScreenEvent
    data class OnReOrderModalShow(val show: Boolean) : LoanScreenEvent
    data object OnAddLoan : LoanScreenEvent
    data object OnLoanModalDismiss : LoanScreenEvent
}
