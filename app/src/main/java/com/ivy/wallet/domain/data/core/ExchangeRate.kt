package com.ivy.wallet.domain.data.core

data class ExchangeRate(
    val baseCurrency: String,
    val currency: String,
    val rate: Double,
)