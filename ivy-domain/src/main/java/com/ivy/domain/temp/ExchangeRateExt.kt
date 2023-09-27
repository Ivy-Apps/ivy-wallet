package com.ivy.domain.temp

import com.ivy.domain.datamodel.ExchangeRate
import com.ivy.persistence.db.entity.ExchangeRateEntity

fun ExchangeRateEntity.toDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate
)