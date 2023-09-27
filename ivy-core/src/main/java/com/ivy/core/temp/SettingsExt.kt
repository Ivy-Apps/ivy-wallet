package com.ivy.core.temp

import com.ivy.core.datamodel.Settings
import com.ivy.persistence.db.entity.SettingsEntity

fun SettingsEntity.toDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)