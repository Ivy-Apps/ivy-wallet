package com.ivy.common

import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnType

fun mapToTrnType(transactionType: TransactionType): TrnType = when (transactionType) {
    TransactionType.Expense -> TrnType.EXPENSE
    TransactionType.Income -> TrnType.INCOME
    is TransactionType.Transfer -> TrnType.TRANSFER
}