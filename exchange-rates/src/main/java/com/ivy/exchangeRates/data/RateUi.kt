package com.ivy.exchangeRates.data

import androidx.compose.runtime.Immutable

@Immutable
data class RateUi(
    val from: String,
    val to: String,
    val rate: Double
)