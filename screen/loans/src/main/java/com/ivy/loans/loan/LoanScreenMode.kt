package com.ivy.loans.loan

import androidx.compose.runtime.Immutable

@Immutable
sealed interface LoanScreenMode {
    data object TabularMode : LoanScreenMode
    data object NonTabularMode : LoanScreenMode
}