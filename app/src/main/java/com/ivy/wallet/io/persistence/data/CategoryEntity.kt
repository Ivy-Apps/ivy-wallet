package com.ivy.wallet.io.persistence.data

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.ui.theme.Ivy
import java.util.*

@Entity(tableName = "categories")
data class CategoryEntity(
    val name: String,
    val color: Int = Ivy.toArgb(),
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