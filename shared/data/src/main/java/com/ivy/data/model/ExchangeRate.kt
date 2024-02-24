package com.ivy.data.model


data class ExchangeRate(
    val baseCurrency: String,
    val currency: String,
    val rate: Double,
    val manualOverride: Boolean
)
