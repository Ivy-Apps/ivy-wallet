package com.ivy.core.persistence.entity.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.SyncState
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import java.time.Instant

@Entity(tableName = "categories")
data class CategoryEntity(
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
    @ColumnInfo(name = "parentCategoryId", index = true)
    val parentCategoryId: String?,
    @ColumnInfo(name = "type", index = true)
    val type: CategoryType,
    @ColumnInfo(name = "state", index = true)
    val state: CategoryState,
    @ColumnInfo(name = "sync", index = true)
    val sync: SyncState,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Instant,
)