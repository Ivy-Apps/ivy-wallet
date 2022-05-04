package com.ivy.wallet.io.network.data

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.io.persistence.data.CategoryEntity
import com.ivy.wallet.ui.theme.Ivy
import java.util.*

data class CategoryDTO(
    val name: String,
    val color: Int = Ivy.toArgb(),
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