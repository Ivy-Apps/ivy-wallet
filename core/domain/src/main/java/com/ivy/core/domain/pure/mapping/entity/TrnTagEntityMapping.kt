package com.ivy.core.domain.pure.mapping.entity

import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState

fun mapToTrnTagEntity(
    trnId: String,
    tagId: String,
    sync: SyncState,
): TrnTagEntity = TrnTagEntity(
    trnId = trnId,
    tagId = tagId,
    sync = sync,
)