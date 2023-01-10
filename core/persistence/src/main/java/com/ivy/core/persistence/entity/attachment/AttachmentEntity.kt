package com.ivy.core.persistence.entity.attachment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import java.time.Instant

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "associatedId", index = true)
    val associatedId: String,
    @ColumnInfo(name = "uri")
    val uri: String,
    @ColumnInfo(name = "source")
    val source: AttachmentSource,
    @ColumnInfo(name = "filename")
    val filename: String?,
    @ColumnInfo(name = "type")
    val type: AttachmentType?,

    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)