package com.ivy.db_persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.Category
import java.util.*

@Entity(tableName = "categories")
data class CategoryEntity(
    val name: String,
    val color: Int,
    val icon: String? = null,
    val orderNum: Double = 0.0,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Category = Category(
        name = name,
        color = color,
        icon = icon,
        orderNum = orderNum,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}