package com.ivy.exchange.cache

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