package com.ivy.core.domain.calculation

import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.PositiveDouble

fun exchangeRates(
    base: String,
    rates: Map<String, Double>
) = ExchangeRates(
    base = AssetCode.of(base),
    rates = rates.map { (asset, value) ->
        AssetCode.of(asset) to PositiveDouble.of(value)
    }.toMap()
)