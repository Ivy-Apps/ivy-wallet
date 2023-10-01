package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.ExchangeRate
import com.ivy.data.db.entity.ExchangeRateEntity

fun ExchangeRateEntity.toDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate
)