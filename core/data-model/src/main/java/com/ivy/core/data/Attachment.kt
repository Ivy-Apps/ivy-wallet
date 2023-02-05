package com.ivy.core.data

import com.ivy.core.data.sync.Syncable
import java.time.LocalDateTime
import java.util.*

data class Attachment(
    val id: UUID,
    val uri: String,
    val name: String,
    override val lastUpdated: LocalDateTime,
) : Syncable

@JvmInline
value class AttachmentId(val id: String)