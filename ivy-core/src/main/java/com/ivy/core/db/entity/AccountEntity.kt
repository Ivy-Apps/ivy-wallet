package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.Account
import com.ivy.core.kotlinxserilzation.KSerializerUUID
import com.ivy.design.l0_system.Green
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.util.*

@Keep
@Serializable
@Entity(tableName = "accounts")
data class AccountEntity(
    @SerialName("name")
    val name: String,
    @SerialName("currency")
    val currency: String? = null,
    @SerialName("color")
    val color: Int = Green.toArgb(),
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("orderNum")
    val orderNum: Double = 0.0,
    @SerialName("includeInBalance")
    val includeInBalance: Boolean = true,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Account = Account(
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
