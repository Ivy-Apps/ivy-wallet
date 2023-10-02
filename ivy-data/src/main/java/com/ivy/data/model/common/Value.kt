package com.ivy.data.model.common

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble

/**
 * Represents monetary value. (like 10 USD, 5 EUR, 0.005 BTC, 12 GOLD_GRAM)
 */
data class Value(
    val amount: PositiveDouble,
    val asset: AssetCode,
)