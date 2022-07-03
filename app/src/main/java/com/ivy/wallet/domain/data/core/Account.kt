package com.ivy.wallet.domain.data.core

import com.ivy.data.Account
import com.ivy.wallet.io.network.data.AccountDTO
import com.ivy.wallet.io.persistence.data.AccountEntity

fun Account.toEntity(): AccountEntity = AccountEntity(
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

fun Account.toDTO(): AccountDTO = AccountDTO(
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    orderNum = orderNum,
    includeInBalance = includeInBalance,
    id = id
)