package com.ivy.wallet.utils

import com.ivy.data.IvyCurrency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.truncate

const val MILLION = 1000000
const val N_100K = 100000
const val THOUSAND = 1000

@Deprecated("don't use!")
fun String.amountToDoubleOrNull(): Double? {
    return this.normalizeAmount().toDoubleOrNull()
}

@Deprecated("don't use!")
fun String.amountToDouble(): Double {
    return this.normalizeAmount().toDouble()
}

@Deprecated("don't use!")
fun String.normalizeAmount(): String {
    return this.removeGroupingSeparator()
        .normalizeDecimalSeparator()
}

@Deprecated("don't use!")
fun String.normalizeExpression(): String {
    return this.removeGroupingSeparator()
        .normalizeDecimalSeparator()
}

@Deprecated("don't use!")
fun String.removeGroupingSeparator(): String {
    return replace(localGroupingSeparator(), "")
}

@Deprecated("don't use!")
fun String.normalizeDecimalSeparator(): String {
    return replace(localDecimalSeparator(), ".")
}

@Deprecated("don't use!")
fun localDecimalSeparator(): String {
    return DecimalFormatSymbols.getInstance().decimalSeparator.toString()
}

@Deprecated("don't use!")
fun localGroupingSeparator(): String {
    return DecimalFormatSymbols.getInstance().groupingSeparator.toString()
}

//Display Formatting
@Deprecated("don't use!")
fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Deprecated("don't use!")
fun Double.format(currencyCode: String): String {
    return this.format(IvyCurrency.fromCode(currencyCode))
}

@Deprecated("don't use!")
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

@Deprecated("don't use!")
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

@Deprecated("don't use!")
private fun Double.formatFIAT(): String = DecimalFormat("#,##0.00").format(this)

@Deprecated("don't use!")
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

@Deprecated("don't use!")
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

@Deprecated("don't use!")
fun hasSignificantDecimalPart(number: Double): Boolean {
    //TODO: Review, might cause trouble when integrating crypto
    val intPart = number.toInt()
    return abs(number - intPart) >= 0.009
}

@Deprecated("don't use!")
fun shouldShortAmount(amount: Double): Boolean {
    return abs(amount) >= N_100K
}

@Deprecated("don't use!")
fun formatInt(number: Int): String {
    return DecimalFormat("#,###,###,###").format(number)
}

@Deprecated("don't use!")
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

@Deprecated("don't use!")
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
