package com.ivy.core.persistence.dummy.trn

import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.CurrencyCode
import com.ivy.data.SyncState
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnType
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun dummyTrnEntity(
    id: String = UUID.randomUUID().toString(),
    accountId: String = "",
    type: TrnType = TrnType.Expense,
    amount: Double = 0.0,
    currency: CurrencyCode = "USD",
    dateTime: Instant = Instant.now().truncatedTo(ChronoUnit.SECONDS),
    dateTimeType: TrnTimeType = TrnTimeType.Actual,
    title: String? = null,
    description: String? = null,
    categoryId: String? = null,
    state: TrnState = TrnState.Default,
    purpose: TrnPurpose? = null,
    sync: SyncState = SyncState.Synced,
): TrnEntity = TrnEntity(
    id = id,
    accountId = accountId,
    type = type,
    amount = amount,
    currency = currency,
    time = dateTime,
    timeType = dateTimeType,
    title = title,
    description = description,
    categoryId = categoryId,
    state = state,
    purpose = purpose,
    sync = sync,
)