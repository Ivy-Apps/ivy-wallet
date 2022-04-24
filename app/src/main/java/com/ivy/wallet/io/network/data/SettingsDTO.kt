package com.ivy.wallet.io.network.data

import com.ivy.design.l0_system.Theme
import com.ivy.wallet.io.persistence.data.SettingsEntity
import java.util.*

data class SettingsDTO(
    val theme: Theme,
    val currency: String,
    val bufferAmount: Double,
    val name: String,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): SettingsEntity = SettingsEntity(
        theme = theme,
        currency = currency,
        bufferAmount = bufferAmount,
        name = name,
        id = id,

        isSynced = true,
        isDeleted = false
    )
}