package com.ivy.core.temp

import com.ivy.core.datamodel.Loan
import com.ivy.persistence.db.entity.LoanEntity
import com.ivy.persistence.model.LoanType

fun LoanEntity.toDomain(): Loan = Loan(
    name = name,
    amount = amount,
    type = type,
    color = color,
    icon = icon,
    orderNum = orderNum,
    accountId = accountId,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)

fun LoanEntity.humanReadableType(): String {
    return if (type == LoanType.BORROW) "BORROWED" else "LENT"
}