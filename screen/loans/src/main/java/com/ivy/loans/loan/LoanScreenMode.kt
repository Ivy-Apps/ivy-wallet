package com.ivy.loans.loan

sealed interface LoanScreenMode {
    data object TabularMode : LoanScreenMode
    data object NonTabularMode : LoanScreenMode
}