package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.Settings
import com.ivy.design.l0_system.Theme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Keep
@Serializable
@Entity(tableName = "settings")
data class SettingsEntity(
    @SerialName("theme")
    val theme: Theme,
    @SerialName("currency")
    val currency: String,
    @SerialName("bufferAmount")
    val bufferAmount: Double,
    @SerialName("name")
    val name: String,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
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
