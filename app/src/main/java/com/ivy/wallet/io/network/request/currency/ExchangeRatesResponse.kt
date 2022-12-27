package com.ivy.wallet.io.network.request.currency

data class ExchangeRatesResponse(
    val date: String,
    val eur: Map<String, Double>,
)