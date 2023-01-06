package com.ivy.core.domain.pure.format

import androidx.compose.runtime.Immutable

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