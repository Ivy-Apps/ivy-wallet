package com.ivy.core.domain.pure.format

import com.ivy.core.domain.pure.isCrypto
import com.ivy.core.domain.pure.util.formatShortened
import com.ivy.data.Value
import java.text.DecimalFormat

fun format(
    value: Value,
    shortenFiat: Boolean,
): FormattedValue = if (isCrypto(value.currency))
    formatCrypto(value) else formatFiat(value = value, shorten = shortenFiat)

private fun formatCrypto(value: Value): FormattedValue {
    tailrec fun removeTrailingZeros(number: String): String = if (number.last() != '0')
        number else removeTrailingZeros(number.dropLast(1))

    val df = DecimalFormat("###,###,##0.${"0".repeat(12)}")
    val amountTrailingZeros = df.format(value.amount)
    return FormattedValue(
        amount = removeTrailingZeros(amountTrailingZeros),
        currency = value.currency
    )
}

private fun formatFiat(
    value: Value,
    shorten: Boolean
): FormattedValue = if (shorten) {
    // shorten to 10k, 10M, etc
    FormattedValue(
        amount = formatShortened(value.amount),
        currency = value.currency
    )
} else {
    val df = DecimalFormat("#,##0.00")
    FormattedValue(
        amount = df.format(value.amount),
        currency = value.currency
    )
}