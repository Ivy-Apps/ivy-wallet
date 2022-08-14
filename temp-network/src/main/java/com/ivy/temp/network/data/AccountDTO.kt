package com.ivy.wallet.io.network.data

import com.ivy.data.AccountOld
import com.ivy.wallet.io.persistence.data.AccountEntity
import java.util.*

data class AccountDTO(
    val name: String,
    val currency: String? = null,
    val color: Int = 0,
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

fun AccountOld.toDTO(): AccountDTO = AccountDTO(
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    orderNum = orderNum,
    includeInBalance = includeInBalance,
    id = id,
)