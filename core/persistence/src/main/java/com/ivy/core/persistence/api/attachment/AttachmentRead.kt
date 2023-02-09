package com.ivy.core.persistence.api.attachment

import com.ivy.core.data.Attachment
import com.ivy.core.data.AttachmentId
import com.ivy.core.persistence.api.ReadSyncable

interface AttachmentRead : ReadSyncable<Attachment, AttachmentId, AttachmentQuery> {
}

sealed interface AttachmentQuery
