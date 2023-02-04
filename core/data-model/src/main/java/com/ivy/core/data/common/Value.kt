package com.ivy.core.data.common

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption

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
 * Use [AssetCode.fromStringUnsafe] to create one.
 */
@JvmInline
value class AssetCode private constructor(val code: String) {
    companion object {
        /**
         * @throws error if the code is blank
         * @return valid trimmed [AssetCode]
         */
        fun fromStringUnsafe(code: String): AssetCode = fromString(code).getOrElse {
            error("AssetCode error: code cannot be blank!")
        }

        fun fromString(code: String): Option<AssetCode> =
            code.takeIf { it.isNotBlank() }.toOption()
                .map { AssetCode(it.trim()) }
    }
}

sealed interface SignedValue {
    val value: Value

    data class Positive(override val value: Value) : SignedValue
    data class Negative(override val value: Value) : SignedValue
    data class Zero(override val value: Value) : SignedValue
}