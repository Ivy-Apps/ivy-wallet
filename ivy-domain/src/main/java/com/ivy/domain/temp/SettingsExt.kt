package com.ivy.domain.temp

import com.ivy.domain.datamodel.Settings
import com.ivy.persistence.db.entity.SettingsEntity

fun SettingsEntity.toDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)