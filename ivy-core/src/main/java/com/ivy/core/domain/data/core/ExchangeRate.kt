package com.ivy.wallet.domain.data.core

import androidx.compose.runtime.Immutable
import com.ivy.wallet.io.persistence.data.ExchangeRateEntity

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
