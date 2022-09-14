package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.AccountOld
import java.util.*

@Deprecated("use `:core:persistence`")
@Entity(tableName = "accounts")
data class AccountEntity(
    val name: String,
    val currency: String? = null,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val includeInBalance: Boolean = true,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): AccountOld = AccountOld(
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

fun AccountOld.toEntity(): AccountEntity = AccountEntity(
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