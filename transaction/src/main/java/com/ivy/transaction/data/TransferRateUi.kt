package com.ivy.transaction.data

import androidx.compose.runtime.Immutable

@Immutable
data class TransferRateUi(
    val fromToText: String,
    val rateText: String,
    val rateValue: Double,
)