package com.ivy.core.persistence.dummy.exchange

import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.data.exchange.ExchangeProvider

fun dummyExchangeRateEntity(
    baseCurrency: String = "USD",
    currency: String = "EUR",
    rate: Double = 1.95,
    provider: ExchangeProvider? = null,
) = ExchangeRateEntity(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate,
    provider = provider
)