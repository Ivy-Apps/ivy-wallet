package com.ivy.common

import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTypeOld

fun mapToTrnType(transactionType: TransactionType): TrnTypeOld = when (transactionType) {
    TransactionType.Expense -> TrnTypeOld.EXPENSE
    TransactionType.Income -> TrnTypeOld.INCOME
    is TransactionType.Transfer -> TrnTypeOld.TRANSFER
}