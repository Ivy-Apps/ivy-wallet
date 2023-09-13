package com.ivy.core.data.model

import androidx.compose.runtime.Immutable
import com.ivy.core.data.db.entity.ExchangeRateEntity

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
