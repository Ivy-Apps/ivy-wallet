package com.ivy.core.persistence.dummy.attachment

import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.data.SyncState
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import java.time.Instant
import java.util.*

fun dummyAttachmentEntity(
    id: String = UUID.randomUUID().toString(),
    associatedId: String = UUID.randomUUID().toString(),
    uri: String = "attachment",
    source: AttachmentSource = AttachmentSource.Remote,
    type: AttachmentType? = null,
    filename: String? = null,
    sync: SyncState = SyncState.Synced,
    lastUpdated: Instant = Instant.now(),
): AttachmentEntity = AttachmentEntity(
    id = id,
    associatedId = associatedId,
    uri = uri,
    source = source,
    type = type,
    filename = filename,
    sync = sync,
    lastUpdated = lastUpdated,
)