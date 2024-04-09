package com.ivy.legacy.datamodel.temp

import com.ivy.data.db.entity.SettingsEntity
import com.ivy.legacy.datamodel.Settings

fun SettingsEntity.toLegacyDomain(): Settings = Settings(
    theme = theme,
    baseCurrency = currency,
    bufferAmount = bufferAmount.toBigDecimal(),
    name = name,
    id = id
)
