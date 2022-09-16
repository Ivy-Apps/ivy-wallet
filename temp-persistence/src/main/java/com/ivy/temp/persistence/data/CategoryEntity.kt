package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.CategoryOld
import java.util.*

@Deprecated("use `:core:persistence`")
@Entity(tableName = "categories")
data class CategoryEntity(
    val name: String,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val parentCategoryId: UUID? = null,

    // TODO: Add CategoryType = Income | Expense | Both

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): CategoryOld = CategoryOld(
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

fun CategoryOld.toEntity(): CategoryEntity = CategoryEntity(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    parentCategoryId = parentCategoryId,
    id = id
)