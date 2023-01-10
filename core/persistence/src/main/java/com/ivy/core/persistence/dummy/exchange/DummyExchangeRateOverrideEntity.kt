package com.ivy.core.persistence.dummy.exchange

import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.data.SyncState
import java.time.Instant

fun dummyExchangeRateOverrideEntity(
    baseCurrency: String = "USD",
    currency: String = "EUR",
    rate: Double = 1.95,
    sync: SyncState = SyncState.Synced,
    lastUpdated: Instant = Instant.now(),
) = ExchangeRateOverrideEntity(
    baseCurrency = baseCurrency,
    currency = currency,
    rate = rate,
    sync = sync,
    lastUpdated = lastUpdated,
)