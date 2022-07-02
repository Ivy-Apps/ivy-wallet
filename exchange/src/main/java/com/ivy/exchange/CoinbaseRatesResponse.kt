package com.ivy.exchange

data class CoinbaseRatesResponse(
    val data: ExchangeRatesData
)

data class ExchangeRatesData(
    val currency: String,
    val rates: Map<String, Double>
)