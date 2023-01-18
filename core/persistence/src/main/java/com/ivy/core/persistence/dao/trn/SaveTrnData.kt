package com.ivy.core.persistence.dao.trn

import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity

data class SaveTrnData(
    val entity: TrnEntity,
    val tags: List<TrnTagEntity>,
    val attachments: List<AttachmentEntity>,
    val metadata: List<TrnMetadataEntity>,
)