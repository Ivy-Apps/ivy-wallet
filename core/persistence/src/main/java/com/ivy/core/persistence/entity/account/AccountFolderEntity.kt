package com.ivy.core.persistence.entity.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import java.time.Instant

@Entity(tableName = "account_folders")
data class AccountFolderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "color")
    val color: Int,
    @ColumnInfo(name = "icon")
    val icon: String?,
    @ColumnInfo(name = "orderNum", index = true)
    val orderNum: Double,

    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)