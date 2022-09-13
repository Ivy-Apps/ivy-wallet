package com.ivy.core.persistence.entity.trn.attachment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.transaction.AttachmentType

@Entity(tableName = "trn_attachments")
data class TrnAttachmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "trnId", index = true)
    val trnId: String,
    @ColumnInfo(name = "attachment")
    val attachment: AttachmentType,
)