package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.Settings
import com.ivy.data.db.entity.SettingsEntity

fun SettingsEntity.toDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)