package com.ivy.data.model

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble

data class ExchangeRate(
    val baseCurrency: AssetCode,
    val currency: AssetCode,
    val rate: PositiveDouble,
    val manualOverride: Boolean,
)
