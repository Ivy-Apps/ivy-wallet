package com.ivy.core.data.common

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
 *
 * Use [AssetCode.of] to create one.
 */
@JvmInline
value class AssetCode private constructor(val code: String) {
    companion object {
        /**
         * @throws error if the code is blank
         * @return valid trimmed [AssetCode]
         */
        fun of(code: String): AssetCode = if (code.isNotBlank())
            AssetCode(code.trim()) else error("AssetCode error: code cannot be blank!")
    }
}