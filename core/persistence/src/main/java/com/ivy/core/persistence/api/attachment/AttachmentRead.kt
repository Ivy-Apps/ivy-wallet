package com.ivy.core.persistence.api.attachment

import com.ivy.core.data.Attachment
import com.ivy.core.data.AttachmentId
import com.ivy.core.persistence.api.Read

interface AttachmentRead : Read<Attachment, AttachmentId, AttachmentRead.Query> {
    sealed interface Query
}