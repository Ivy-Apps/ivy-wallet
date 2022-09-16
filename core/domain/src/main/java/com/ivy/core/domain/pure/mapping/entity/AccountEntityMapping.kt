package com.ivy.core.domain.pure.mapping.entity

import com.ivy.core.domain.pure.util.iconId
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.account.Account

fun mapToEntity(acc: Account): AccountEntity = with(acc) {
    AccountEntity(
        id = id.toString(),
        name = name,
        currency = currency,
        color = color,
        icon = icon.iconId(),
        folderId = folderId?.toString(),
        orderNum = orderNum,
        excluded = excluded,
        state = state,
        sync = sync,
    )
}