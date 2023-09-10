package com.ivy.wallet.domain.data.core

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.toArgb
import com.ivy.design.l0_system.Ivy
import com.ivy.wallet.io.persistence.data.CategoryEntity
import java.util.UUID

@Immutable
data class Category(
    val name: String,
    val color: Int = Ivy.toArgb(),
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
