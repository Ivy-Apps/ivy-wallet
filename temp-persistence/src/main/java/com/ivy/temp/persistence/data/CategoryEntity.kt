package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.Category
import java.util.*

@Entity(tableName = "categories")
data class CategoryEntity(
    val name: String,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val parentCategoryId : UUID? = null,

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
        parentCategoryId = parentCategoryId,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    parentCategoryId = parentCategoryId,
    id = id
)