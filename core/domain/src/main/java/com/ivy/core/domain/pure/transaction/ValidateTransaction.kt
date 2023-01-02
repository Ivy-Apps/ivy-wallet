package com.ivy.core.domain.pure.transaction

import com.ivy.data.transaction.Transaction

fun validateTransaction(trn: Transaction): Boolean {
    if (trn.value.amount <= 0) return false
    return true
}