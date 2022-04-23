package com.ivy.wallet.domain.data.core

import com.ivy.wallet.io.network.data.ExchangeRateDTO
import com.ivy.wallet.io.persistence.data.ExchangeRateEntity

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

    fun toDTO(): ExchangeRateDTO = ExchangeRateDTO(
        baseCurrency = baseCurrency,
        currency = currency,
        rate = rate
    )
}