package com.ivy.core.persistence.dummy.trn

import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.data.SyncState
import java.time.Instant
import java.util.*

fun dummyTrnMetadataEntity(
    id: String = UUID.randomUUID().toString(),
    trnId: String = UUID.randomUUID().toString(),
    key: String = UUID.randomUUID().toString(),
    value: String = UUID.randomUUID().toString(),
    sync: SyncState = SyncState.Synced,
    lastUpdated: Instant = Instant.now(),
) = TrnMetadataEntity(
    id = id, trnId = trnId, key = key, value = value, sync = sync,
    lastUpdated = lastUpdated
)