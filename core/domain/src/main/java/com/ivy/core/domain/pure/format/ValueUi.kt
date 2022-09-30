package com.ivy.core.domain.pure.format

data class ValueUi(
    val amount: String,
    val currency: String,
)

fun dummyValueUi(
    amount: String = "0.0",
    currency: String = "USD"
) = ValueUi(
    amount = amount, currency = currency,
)