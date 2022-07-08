package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.Settings
import com.ivy.data.Theme
import java.util.*

@Entity(tableName = "settings")
data class SettingsEntity(
    val theme: Theme,
    val currency: String,
    val bufferAmount: Double,
    val name: String,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
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

fun Settings.toEntity(): SettingsEntity = SettingsEntity(
    theme = theme,
    currency = baseCurrency,
    bufferAmount = bufferAmount.toDouble(),
    name = name,
    id = id,

    isSynced = true,
    isDeleted = false
)