package com.ivy.core.domain.pure.format

import com.ivy.core.domain.pure.isCrypto
import com.ivy.core.domain.pure.util.formatShortened
import com.ivy.data.Value
import java.text.DecimalFormat

fun format(
    value: Value,
    shortenFiat: Boolean,
): ValueUi = if (isCrypto(value.currency))
    formatCrypto(value) else formatFiat(value = value, shorten = shortenFiat)

private fun formatCrypto(value: Value): ValueUi {
    val df = DecimalFormat("###,###,##0.${"#".repeat(16)}")
    return ValueUi(
        amount = df.format(value.amount),
        currency = value.currency
    )
}

private fun formatFiat(
    value: Value,
    shorten: Boolean
): ValueUi = if (shorten) {
    // shorten to 10k, 10M, etc
    ValueUi(
        amount = formatShortened(value.amount),
        currency = value.currency
    )
} else {
    val df = DecimalFormat("###,##0.##")
    ValueUi(
        amount = df.format(value.amount),
        currency = value.currency
    )
}