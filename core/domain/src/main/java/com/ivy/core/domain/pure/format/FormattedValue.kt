package com.ivy.core.domain.pure.format

data class FormattedValue(
    val amount: String,
    val currency: String,
)

fun dummyFormattedValue(
    amount: String = "0.0",
    currency: String = "USD"
) = FormattedValue(
    amount = amount, currency = currency,
)