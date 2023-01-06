package com.ivy.transaction.data

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode

@Immutable
data class TransferRateUi(
    val rateValue: Double,
    val rateValueFormatted: String,
    val fromCurrency: CurrencyCode,
    val toCurrency: CurrencyCode,
)