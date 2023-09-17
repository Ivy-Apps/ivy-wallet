package com.ivy.core.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.core.db.entity.ExchangeRateEntity

@Immutable
data class ExchangeRate(
    val baseCurrency: String,
    val currency: String,
    val rate: Double,
) {
    fun toEntity(): ExchangeRateEntity = ExchangeRateEntity(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}
