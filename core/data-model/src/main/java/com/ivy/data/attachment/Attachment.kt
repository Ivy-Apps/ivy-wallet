package com.ivy.data.attachment

import com.ivy.data.Sync

@Deprecated("will be removed!")
data class Attachment(
    val id: String,
    val associatedId: String,
    val uri: String,
    val source: AttachmentSource,
    val filename: String?,
    val type: AttachmentType?,
    val sync: Sync,
)