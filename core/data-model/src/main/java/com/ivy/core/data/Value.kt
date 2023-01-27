package com.ivy.core.data

/**
 * Represents monetary value. (like 10 USD, 5 EUR, 0.005 BTC, 12 GOLD_GRAM)
 */
data class Value(
    val amount: PositiveDouble,
    val asset: AssetCode,
)

/**
 * A unique string code representing an asset:
 * - fiat currency (like EUR, USD, GBP)
 * - crypto currency (like BTC, ETH, ADA)
 * - something abstract (like GOLD, WATER, BMW)
 */
@JvmInline
value class AssetCode(val code: String)