package com.ivy.core.domain.calculation

import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.PositiveDouble

fun exchangeRates(
    base: String,
    rates: Map<String, Double>
) = ExchangeRates(
    base = AssetCode.fromStringUnsafe(base),
    rates = rates.map { (asset, value) ->
        AssetCode.fromStringUnsafe(asset) to PositiveDouble.fromDoubleUnsafe(value)
    }.toMap()
)