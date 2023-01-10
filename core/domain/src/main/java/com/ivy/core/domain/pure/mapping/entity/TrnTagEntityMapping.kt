package com.ivy.core.domain.pure.mapping.entity

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.Sync

fun mapToTrnTagEntity(
    trnId: String,
    tagId: String,
    sync: Sync,
    timeProvider: TimeProvider,
): TrnTagEntity = TrnTagEntity(
    trnId = trnId,
    tagId = tagId,
    sync = sync.state,
    lastUpdated = sync.lastUpdated.toUtc(timeProvider)
)