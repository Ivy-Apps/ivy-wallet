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
    tailrec fun removeTrailingZeros(number: String): String = if (number.lastOrNull() != '0')
        number else removeTrailingZeros(number.dropLast(1))

    val df = DecimalFormat("###,###,##0.${"0".repeat(12)}")
    val amountTrailingZeros = df.format(value.amount)
    return ValueUi(
        amount = removeTrailingZeros(amountTrailingZeros),
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
    val df = DecimalFormat("#,##0.00")
    ValueUi(
        amount = df.format(value.amount),
        currency = value.currency
    )
}