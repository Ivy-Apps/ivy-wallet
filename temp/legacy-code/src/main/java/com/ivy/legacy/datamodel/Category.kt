package com.ivy.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.data.db.entity.CategoryEntity
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class Category(
    val name: String,
    val color: Int,
    val icon: String? = null,
    val orderNum: Double = 0.0,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): CategoryEntity = CategoryEntity(
        name = name,
        color = color,
        icon = icon,
        orderNum = orderNum,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
