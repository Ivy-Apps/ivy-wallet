package com.ivy.core.domain.pure.dummy

import androidx.annotation.ColorInt
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import java.time.LocalDateTime
import java.util.*

fun dummyAcc(
    id: UUID = UUID.randomUUID(),
    name: String = "Dummy acc",
    currency: CurrencyCode = "USD",
    @ColorInt
    color: Int = 1,
    icon: ItemIconId = "account",
    folderId: UUID? = null,
    excluded: Boolean = false,
    orderNum: Double = 0.0,
    sync: SyncState = SyncState.Synced,
    lastUpdated: LocalDateTime = LocalDateTime.now(),
    state: AccountState = AccountState.Default,
): Account = Account(
    id = id,
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    folderId = folderId,
    excluded = excluded,
    orderNum = orderNum,
    sync = Sync(sync, lastUpdated),
    state = state,
)
