package com.ivy.wallet.domain.data.core

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.io.network.data.AccountDTO
import com.ivy.wallet.io.persistence.data.AccountEntity
import com.ivy.wallet.ui.theme.Green
import java.util.*

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

    fun toDTO(): AccountDTO = AccountDTO(
        name = name,
        currency = currency,
        color = color,
        icon = icon,
        orderNum = orderNum,
        includeInBalance = includeInBalance,
        id = id
    )
}