package com.ivy.core.domain.pure.format

import androidx.compose.runtime.Immutable
import com.ivy.data.Value

@Immutable
data class CombinedValueUi constructor(
    val value: Value,
    val valueUi: ValueUi,
) {
    companion object {
        fun initial() = CombinedValueUi(
            value = Value(amount = 0.0, currency = ""),
            valueUi = ValueUi(amount = "0.0", currency = ""),
        )
    }

    constructor(
        amount: Double,
        currency: String,
        shortenFiat: Boolean,
    ) : this(
        value = Value(amount, currency),
        shortenFiat = shortenFiat,
    )

    constructor(
        value: Value,
        shortenFiat: Boolean,
    ) : this(
        value = value,
        valueUi = format(value, shortenFiat = shortenFiat),
    )
}

fun dummyCombinedValueUi(
    amount: Double = 0.0,
    currency: String = "USD",
    shortenFiat: Boolean = false,
) = CombinedValueUi(
    amount = amount,
    currency = currency,
    shortenFiat = shortenFiat,
)