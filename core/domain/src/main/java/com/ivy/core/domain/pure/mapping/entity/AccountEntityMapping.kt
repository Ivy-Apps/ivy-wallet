package com.ivy.core.domain.pure.mapping.entity

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.account.Account

fun mapToEntity(
    acc: Account,
    timeProvider: TimeProvider,
): AccountEntity = with(acc) {
    AccountEntity(
        id = id.toString(),
        name = name,
        currency = currency,
        color = color,
        icon = icon,
        folderId = folderId?.toString(),
        orderNum = orderNum,
        excluded = excluded,
        state = state,
        sync = sync.state,
        lastUpdated = sync.lastUpdated.toUtc(timeProvider),
    )
}