package com.ivy.core.temp

import com.ivy.core.datamodel.ExchangeRate
import com.ivy.persistence.db.entity.ExchangeRateEntity

fun ExchangeRateEntity.toDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate
)