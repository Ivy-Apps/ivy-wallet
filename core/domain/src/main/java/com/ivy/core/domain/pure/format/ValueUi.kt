package com.ivy.core.domain.pure.format

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SignedValueUi {
    val value: ValueUi

    @Immutable
    data class Positive(override val value: ValueUi) : SignedValueUi

    @Immutable
    data class Zero(override val value: ValueUi) : SignedValueUi

    @Immutable
    data class Negative(override val value: ValueUi) : SignedValueUi
}

@Immutable
data class ValueUi(
    val amount: String,
    val currency: String,
)

fun dummyValueUi(
    amount: String = "0",
    currency: String = "USD"
) = ValueUi(
    amount = amount, currency = currency,
)