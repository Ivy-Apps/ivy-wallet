package com.ivy.core.persistence.entity.trn

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.ivy.data.SyncState
import java.time.Instant

@Entity(tableName = "trn_tags", primaryKeys = ["trnId", "tagId"])
data class TrnTagEntity(
    @ColumnInfo(name = "trnId", index = true)
    val trnId: String,
    @ColumnInfo(name = "tagId", index = true)
    val tagId: String,
    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)