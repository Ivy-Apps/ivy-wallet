package com.ivy.exchange.coinbase

import com.google.gson.annotations.SerializedName

data class CoinbaseRatesResponse(
    @SerializedName("data")
    val data: ExchangeRatesData
)

data class ExchangeRatesData(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
)