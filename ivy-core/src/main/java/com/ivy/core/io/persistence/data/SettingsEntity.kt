package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.domain.data.core.Settings
import java.util.*

@Keep
@Entity(tableName = "settings")
data class SettingsEntity(
    @SerializedName("theme")
    val theme: Theme,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("bufferAmount")
    val bufferAmount: Double,
    @SerializedName("name")
    val name: String,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Settings = Settings(
        theme = theme,
        baseCurrency = currency,
        bufferAmount = bufferAmount.toBigDecimal(),
        name = name,
        id = id
    )
}
