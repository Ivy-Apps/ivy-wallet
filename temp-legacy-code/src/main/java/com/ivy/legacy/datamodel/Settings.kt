package com.ivy.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.Theme
import com.ivy.data.db.entity.SettingsEntity
import java.math.BigDecimal
import java.util.UUID

@Deprecated("Legacy data model. Will be deleted")
@Immutable
data class Settings(
    val theme: Theme,
    val baseCurrency: String,
    val bufferAmount: BigDecimal,
    val name: String,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): SettingsEntity = SettingsEntity(
        theme = theme,
        currency = baseCurrency,
        bufferAmount = bufferAmount.toDouble(),
        name = name,
        id = id
    )
}
