package com.ivy.core.domain.pure.transaction.transfer

import com.ivy.core.domain.action.transaction.transfer.TransferData

fun validateTransfer(data: TransferData): Boolean {
    if (data.accountFrom == data.accountTo) return false
    if (data.amountFrom.amount <= 0) return false
    if (data.amountTo.amount <= 0) return false
    if (data.fee != null && data.fee.amount <= 0) return false
    return true
}