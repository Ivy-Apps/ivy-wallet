package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.ui.theme.Green
import java.util.*

@Keep
@Entity(tableName = "accounts")
data class AccountEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("color")
    val color: Int = Green.toArgb(),
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("orderNum")
    val orderNum: Double = 0.0,
    @SerializedName("includeInBalance")
    val includeInBalance: Boolean = true,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
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
