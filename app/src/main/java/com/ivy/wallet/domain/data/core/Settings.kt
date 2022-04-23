package com.ivy.wallet.domain.data.core

import com.ivy.design.l0_system.Theme
import com.ivy.wallet.io.network.data.SettingsDTO
import com.ivy.wallet.io.persistence.data.SettingsEntity
import java.math.BigDecimal
import java.util.*

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

    fun toDTO(): SettingsDTO = SettingsDTO(
        theme = theme,
        currency = baseCurrency,
        bufferAmount = bufferAmount.toDouble(),
        name = name,
        id = id
    )
}