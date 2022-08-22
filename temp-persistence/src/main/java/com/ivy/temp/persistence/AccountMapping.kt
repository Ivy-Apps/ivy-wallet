package com.ivy.temp.persistence

import com.ivy.core.functions.icon.iconId
import com.ivy.data.account.Account
import com.ivy.wallet.io.persistence.data.AccountEntity

fun mapToEntity(acc: Account): AccountEntity = AccountEntity(
    id = acc.id,
    name = acc.name,
    currency = acc.currency,
    color = acc.color,
    icon = acc.icon.iconId(),
    orderNum = acc.metadata.orderNum,
    includeInBalance = !acc.excluded,
    isSynced = acc.metadata.sync.isSynced,
    isDeleted = acc.metadata.sync.isDeleted
)