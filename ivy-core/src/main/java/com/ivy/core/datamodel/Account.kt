package com.ivy.core.datamodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.toArgb
import com.ivy.design.l0_system.Green
import com.ivy.core.db.entity.AccountEntity
import java.util.UUID

@Immutable
data class Account(
    val name: String,
    val currency: String? = null,
    val color: Int = Green.toArgb(),
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val includeInBalance: Boolean = true,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): AccountEntity = AccountEntity(
        name = name,
        currency = currency,
        color = color,
        icon = icon,
        orderNum = orderNum,
        includeInBalance = includeInBalance,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
