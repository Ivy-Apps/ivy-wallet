package com.ivy.wallet.io.network.request.currency

import androidx.annotation.Keep

@Keep
data class ExchangeRatesResponse(
    val date: String,
    val eur: Map<String, Double>,
)