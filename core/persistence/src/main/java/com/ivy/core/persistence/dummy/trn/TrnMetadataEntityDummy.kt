package com.ivy.core.persistence.dummy.trn

import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import java.util.*

fun dummyTrnMetadataEntity(
    id: String = UUID.randomUUID().toString(),
    trnId: String = UUID.randomUUID().toString(),
    key: String = UUID.randomUUID().toString(),
    value: String = UUID.randomUUID().toString(),
) = TrnMetadataEntity(
    id = id, trnId = trnId, key = key, value = value
)