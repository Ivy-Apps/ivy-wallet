package com.ivy.core.persistence.dao.trn

import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity

data class SaveTrnData(
    val entity: TransactionEntity,
    val tags: List<TrnTagEntity>,
    val attachments: List<AttachmentEntity>,
    val metadata: List<TrnMetadataEntity>,
)