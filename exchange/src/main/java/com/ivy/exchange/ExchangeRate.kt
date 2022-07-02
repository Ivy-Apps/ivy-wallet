package com.ivy.exchange

import com.ivy.wallet.io.network.data.ExchangeRateDTO

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