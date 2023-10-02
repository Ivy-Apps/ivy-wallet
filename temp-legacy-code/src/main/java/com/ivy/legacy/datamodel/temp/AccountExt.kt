package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.Account
import com.ivy.data.db.entity.AccountEntity

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