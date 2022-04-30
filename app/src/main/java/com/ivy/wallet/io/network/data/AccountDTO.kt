package com.ivy.wallet.io.network.data

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.io.persistence.data.AccountEntity
import com.ivy.wallet.ui.theme.Green
import java.util.*

data class AccountDTO(
    val name: String,
    val currency: String? = null,
    val color: Int = Green.toArgb(),
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val includeInBalance: Boolean = true,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): AccountEntity = AccountEntity(
        name = name,
        currency = currency,
        color = color,
        icon = icon,
        orderNum = orderNum,
        includeInBalance = includeInBalance,
        id = id,
        isSynced = true,
        isDeleted = false
    )
}

