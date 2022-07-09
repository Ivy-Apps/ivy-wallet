package com.ivy.base

import com.ivy.data.loan.Loan
import com.ivy.data.loan.LoanType

fun Loan.humanReadableType(): String {
    return if (type == LoanType.BORROW) "BORROWED" else "LENT"
}