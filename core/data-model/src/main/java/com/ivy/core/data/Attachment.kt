package com.ivy.core.data

import java.util.*

data class Attachment(
    val id: UUID,
    val uri: String,
    val name: String,
)

@JvmInline
value class AttachmentId(val id: String)