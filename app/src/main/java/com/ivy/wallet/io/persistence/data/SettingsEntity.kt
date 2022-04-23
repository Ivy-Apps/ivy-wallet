package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.domain.data.core.Settings
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