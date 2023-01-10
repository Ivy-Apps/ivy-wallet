package com.ivy.core.persistence.dummy.account

import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.SyncState
import com.ivy.data.account.AccountState
import java.time.Instant
import java.util.*

fun dummyAccountEntity(
    id: String = UUID.randomUUID().toString(),
    name: String = "account",
    currency: String = "currency",
    color: Int = 123123,
    icon: String? = "icon",
    folderId: String? = null,
    orderNum: Double = 0.0,
    excluded: Boolean = false,
    state: AccountState = AccountState.Default,
    sync: SyncState = SyncState.Synced,
    lastUpdated: Instant = Instant.now(),
): AccountEntity = AccountEntity(
    id = id,
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    folderId = folderId,
    orderNum = orderNum,
    excluded = excluded,
    state = state,
    sync = sync,
    lastUpdated = lastUpdated,
)
