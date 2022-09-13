package com.ivy.core.persistence.dummy.trn

import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import java.util.*

fun dummyTrnLinkRecordEntity(
    id: String = UUID.randomUUID().toString(),
    trnId: String = UUID.randomUUID().toString(),
    linkId: String = UUID.randomUUID().toString(),
) = TrnLinkRecordEntity(
    id = id, trnId = trnId, linkId = linkId,
)