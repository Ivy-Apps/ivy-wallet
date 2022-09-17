package com.ivy.core.domain.pure.format

import com.ivy.core.domain.pure.isCrypto
import com.ivy.data.IvyCurrency
import com.ivy.data.Value
import com.ivy.wallet.utils.*
import com.ivy.wallet.utils.format
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.truncate

fun format(
    value: Value,
    shortenNonCrypto: Boolean,
): FormattedValue = if (isCrypto(value.currency))
    formatCrypto(value) else formatFiat(value = value, shorten = shortenNonCrypto)

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
    TODO()
} else {
    val df = DecimalFormat("#,##0.00")
    FormattedValue(
        amount = df.format(value.amount),
        currency = value.currency
    )
}

// region Old
fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun Double.format(currencyCode: String): String {
    return this.format(IvyCurrency.fromCode(currencyCode))
}

fun Double.format(currency: IvyCurrency?): String {
    return if (currency?.isCrypto == true) {
        val result = this.formatCrypto()
        return when {
            result.lastOrNull() == localDecimalSeparator().firstOrNull() -> {
                val newResult = result.dropLast(1)
                if (newResult.isEmpty()) "0" else newResult
            }
            result.isEmpty() -> {
                "0"
            }
            else -> result
        }
    } else {
        formatFIAT()
    }
}

fun Double.formatCrypto(): String {
    val pattern = "###,###,##0.${"0".repeat(9)}"
    val format = DecimalFormat(pattern)
    val numberStringWithZeros = format.format(this)

    var lastTrailingZeroIndex: Int? = null
    for (i in numberStringWithZeros.lastIndex.downTo(0)) {
        if (numberStringWithZeros[i] == '0') {
            lastTrailingZeroIndex = i
        } else {
            break
        }
    }

    return if (lastTrailingZeroIndex != null)
        numberStringWithZeros.substring(0, lastTrailingZeroIndex) else numberStringWithZeros
}

private fun Double.formatFIAT(): String = DecimalFormat("#,##0.00").format(this)

fun shortenAmount(amount: Double): String {
    return when {
        abs(amount) >= MILLION -> {
            formatShortenedNumber(amount / MILLION, "m")
        }
        abs(amount) >= THOUSAND -> {
            formatShortenedNumber(amount / THOUSAND, "k")
        }
        else -> amount.toString()
    }
}

private fun formatShortenedNumber(
    number: Double,
    extension: String
): String {
    return if (hasSignificantDecimalPart(number)) {
        "${number.format(2)}$extension"
    } else {
        "${number.toInt()}$extension"
    }
}

fun hasSignificantDecimalPart(number: Double): Boolean {
    //TODO: Review, might cause trouble when integrating crypto
    val intPart = number.toInt()
    return abs(number - intPart) >= 0.009
}

fun shouldShortAmount(amount: Double): Boolean {
    return abs(amount) >= N_100K
}

fun formatInt(number: Int): String {
    return DecimalFormat("#,###,###,###").format(number)
}

fun decimalPartFormatted(currency: String, value: Double): String {
    return if (IvyCurrency.fromCode(currency)?.isCrypto == true) {
        val decimalPartFormatted = value.formatCrypto()
            .split(localDecimalSeparator())
            .getOrNull(1) ?: "null"
        if (decimalPartFormatted.isNotBlank())
            "${localDecimalSeparator()}$decimalPartFormatted" else ""
    } else {
        "${localDecimalSeparator()}${decimalPartFormattedFIAT(value)}"
    }
}

private fun decimalPartFormattedFIAT(value: Double): String {
    return DecimalFormat(".00").format(value)
        .split(localDecimalSeparator())
        .getOrNull(1)
        ?: value.toString()
            .split(localDecimalSeparator())
            .getOrNull(1)
        ?: "null"
}

fun Long.length() = when (this) {
    0L -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun formatInputAmount(
    currency: String,
    amount: String,
    newSymbol: String,
    decimalCountMax: Int = 2,
): String? {
    val newlyEnteredNumberString = amount + newSymbol

    val decimalPartString = newlyEnteredNumberString
        .split(localDecimalSeparator())
        .getOrNull(1)
    val decimalCount = decimalPartString?.length ?: 0

    val amountDouble = newlyEnteredNumberString.amountToDoubleOrNull()

    val decimalCountOkay = IvyCurrency.fromCode(currency)?.isCrypto == true
            || decimalCount <= decimalCountMax
    if (amountDouble != null && decimalCountOkay) {
        val intPart = truncate(amountDouble).toInt()
        val decimalPartFormatted = if (decimalPartString != null) {
            "${localDecimalSeparator()}${decimalPartString}"
        } else ""

        return formatInt(intPart) + decimalPartFormatted
    }

    return null
}

fun removeExtraDecimals(
    amount: String,
): String = amount
    .split(localDecimalSeparator())
    .take(2)
    .joinToString(separator = localDecimalSeparator())
// endregion