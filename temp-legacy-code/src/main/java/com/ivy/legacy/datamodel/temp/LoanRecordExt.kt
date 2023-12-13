package com.ivy.legacy.datamodel.temp

import com.ivy.data.db.entity.LoanRecordEntity
import com.ivy.legacy.datamodel.LoanRecord

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
