package com.ivy.core.temp

import com.ivy.core.datamodel.LoanRecord
import com.ivy.persistence.db.entity.LoanRecordEntity

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