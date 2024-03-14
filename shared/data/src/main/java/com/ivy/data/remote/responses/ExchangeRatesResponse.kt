package com.ivy.data.remote.responses

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ExchangeRatesResponse(
    val date: String,
    val rates: Map<String, Double>
)