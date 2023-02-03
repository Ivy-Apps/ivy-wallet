package com.ivy.core.data.calculation

import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.PositiveDouble

/**
 * Provides exchange rates for a given base.
 * A rate tells you how much **1 "CODE" asset unit** is worth in **Y "BASE" asset**.
 *
 * Example:
 * ```
 * {
 *   base: "BGN",
 *   rates: {
 *      "EUR": 0.51,
 *      "USD": 0.56,
 *      "BGN": 1.0
 *   }
 * }
 * ```
 *
 * Exchange rates of 0 aren't allowed because they can lead to [Double.POSITIVE_INFINITY] or
 * [Double.NaN].
 */
data class ExchangeRates(
    val base: AssetCode,
    val rates: Map<AssetCode, PositiveDouble>
)