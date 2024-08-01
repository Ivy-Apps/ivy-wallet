package com.ivy.legacy.datamodel.temp

import com.ivy.data.db.entity.LoanEntity
import com.ivy.data.model.LoanType
import com.ivy.legacy.datamodel.Loan

fun LoanEntity.toLegacyDomain(): Loan = Loan(
    name = name,
    amount = amount,
    type = type,
    color = color,
    icon = icon,
    orderNum = orderNum,
    accountId = accountId,
    note = note,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id,
    dateTime = dateTime
)

fun LoanEntity.humanReadableType(): String {
    return if (type == LoanType.BORROW) "BORROWED" else "LENT"
}
