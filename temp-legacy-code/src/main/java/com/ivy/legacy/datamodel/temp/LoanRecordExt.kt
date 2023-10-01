package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.LoanRecord
import com.ivy.data.db.entity.LoanRecordEntity

fun LoanRecordEntity.toDomain(): LoanRecord = LoanRecord(
    loanId = loanId,
    amount = amount,
    note = note,
    dateTime = dateTime,
    interest = interest,
    accountId = accountId,
    convertedAmount = convertedAmount,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)