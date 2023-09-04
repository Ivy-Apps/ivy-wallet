package com.ivy.wallet.ui.exchangerates.data

import androidx.compose.runtime.Immutable

@Immutable
data class RateUi(
    val from: String,
    val to: String,
    val rate: Double
)
