package com.ivy.core.persistence.entity.tag

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import com.ivy.data.tag.TagState
import java.time.Instant

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", index = true)
    val id: String,
    @ColorInt
    @ColumnInfo(name = "color")
    val color: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "orderNum", index = true)
    val orderNum: Double,
    @ColumnInfo(name = "state", index = true)
    val state: TagState,
    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)