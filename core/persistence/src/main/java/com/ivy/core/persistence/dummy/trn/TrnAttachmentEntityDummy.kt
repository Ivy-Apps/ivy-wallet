package com.ivy.core.persistence.dummy.trn

import com.ivy.core.persistence.entity.trn.attachment.TrnAttachmentEntity
import com.ivy.data.transaction.AttachmentType
import java.util.*

fun dummyTrnAttachmentEntity(
    id: String = UUID.randomUUID().toString(),
    trnId: String = UUID.randomUUID().toString(),
    type: AttachmentType = AttachmentType.File,
    attachment: String = "attachment"
): TrnAttachmentEntity = TrnAttachmentEntity(
    id = id, trnId = trnId, type = type, attachment = attachment
)