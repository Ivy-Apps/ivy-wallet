package com.ivy.domain.temp

import com.ivy.domain.datamodel.Account
import com.ivy.persistence.db.entity.AccountEntity

fun AccountEntity.toDomain(): Account = Account(
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    orderNum = orderNum,
    includeInBalance = includeInBalance,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)