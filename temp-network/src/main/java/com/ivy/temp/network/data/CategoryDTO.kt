package com.ivy.wallet.io.network.data

import com.ivy.data.CategoryOld
import com.ivy.wallet.io.persistence.data.CategoryEntity
import java.util.*

data class CategoryDTO(
    val name: String,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): CategoryEntity = CategoryEntity(
        name = name,
        color = color,
        icon = icon,
        orderNum = orderNum,
        isSynced = true,
        isDeleted = false,
        id = id
    )
}

fun CategoryOld.toDTO(): CategoryDTO = CategoryDTO(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    id = id
)