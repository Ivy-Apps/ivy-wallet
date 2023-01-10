package com.ivy.core.persistence.entity.trn

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import java.time.Instant

@Entity(tableName = "trn_links")
data class TrnLinkRecordEntity(
    /**
     * record id for uniqueness in the records table
     */
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    @ColumnInfo(name = "trnId", index = true)
    val trnId: String,
    @ColumnInfo(name = "batchId")
    val batchId: String,

    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)