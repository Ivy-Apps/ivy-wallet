package com.ivy.data.remote.responses

data class ExchangeRatesResponse(
    val date: String,
    val rates: Map<String, Double>
)