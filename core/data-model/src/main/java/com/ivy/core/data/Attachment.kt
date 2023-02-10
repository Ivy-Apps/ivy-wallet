package com.ivy.core.data

import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId
import java.net.URL
import java.time.LocalDateTime
import java.util.*

data class Attachment(
    override val id: AttachmentId,
    val url: URL,
    val name: String,
    val type: AttachmentType,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Syncable

enum class AttachmentType(val code: Int) {
    Image(1),
    Other(100);

    companion object {
        fun fromCode(code: Int): AttachmentType = when (code) {
            1 -> Image
            else -> Other
        }
    }
}

@JvmInline
value class AttachmentId(override val uuid: UUID) : UniqueId