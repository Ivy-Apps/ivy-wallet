package com.ivy.wallet.io.network.request.currency

data class CoinbaseRatesResponse(
    val data: ExchangeRatesData
)

data class ExchangeRatesData(
    val currency: String,
    val rates: Map<String, Double>
)