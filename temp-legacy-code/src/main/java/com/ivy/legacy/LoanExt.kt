package com.ivy.legacy

import com.ivy.core.datamodel.Loan
import com.ivy.core.datamodel.LoanType
import com.ivy.core.util.stringRes
import com.ivy.resources.R

fun Loan.humanReadableType(): String {
    return if (type == LoanType.BORROW) {
        stringRes(R.string.borrowed_uppercase)
    } else {
        stringRes(R.string.lent_uppercase)
    }
}