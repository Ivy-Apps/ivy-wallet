package com.ivy.legacy.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.data.db.entity.ExchangeRateEntity

@Deprecated("Legacy data model. Will be deleted")
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
