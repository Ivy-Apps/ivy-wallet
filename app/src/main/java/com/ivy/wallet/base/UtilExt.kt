package com.ivy.wallet.base

import android.app.KeyguardManager
import android.content.Context
import android.icu.util.Currency
import com.ivy.wallet.model.IvyCurrency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.log10

const val MILLION = 1000000
const val N_100K = 100000
const val THOUSAND = 1000

fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)

fun String.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

fun String.base64toBytes(): ByteArray = Base64.getDecoder().decode(this)

fun String.base64toString() = String(this.base64toBytes())

fun <T> List<T>?.nullifyEmpty(): MutableList<T>? {
    return when {
        this.isNullOrEmpty() -> null
        this is MutableList -> this
        else -> this.toMutableList()
    }
}

fun <T> MutableSet<T>?.nullifyEmpty(): MutableSet<T>? {
    return if (this.isNullOrEmpty()) null else this
}

fun <T> Collection<T>?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun <T> MutableCollection<T>.update(newItems: Collection<T>) {
    clear()
    addAll(newItems)
}

fun <K, V> MutableMap<K, V>.update(newItems: Map<K, V>) {
    clear()
    putAll(newItems)
}

fun Int.digitsCount() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun <T> MutableList<T>.deepClone(copy: (T) -> T) = this.map { copy(it) }.toMutableList()

fun <T> MutableList<T>.swap(fromIndex: Int, toIndex: Int) {
    val from = this[fromIndex]
    val to = this[toIndex]

    this[fromIndex] = to
    this[toIndex] = from
}

fun numberBetween(min: Double, max: Double): Double {
    return Random().nextDouble() * (max - min) + min;
}

fun <T> MutableList<T>?.orEmpty(): MutableList<T> {
    return this ?: mutableListOf()
}

fun String.nullifyEmpty() = if (this.isBlank()) null else this

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun Double.format(currencyCode: String): String {
    return this.format(IvyCurrency.fromCode(currencyCode))
}

fun Double.format(currency: IvyCurrency?): String {
    return if (currency?.isCrypto == true) {
        val result = this.formatCrypto()
        return when {
            result.lastOrNull() == '.' -> {
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

fun Long.length() = when (this) {
    0L -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

private fun Double.formatFIAT(): String = DecimalFormat("#,##0.00").format(this)

fun getDefaultFIATCurrency(): Currency =
    Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")

fun decimalPartFormatted(currency: String, value: Double): String {
    return if (IvyCurrency.fromCode(currency)?.isCrypto == true) {
        val decimalPartFormatted = value.formatCrypto()
            .split(".")
            .getOrNull(1) ?: "null"
        if (decimalPartFormatted.isNotBlank()) ".$decimalPartFormatted" else ""
    } else {
        ".${decimalPartFormattedFIAT(value)}"
    }
}

private fun decimalPartFormattedFIAT(value: Double): String {
    return DecimalFormat(".00").format(value).split(".", ",").getOrNull(1)
        ?: value.toString().split(".", ",").getOrNull(1) ?: "null"
}

fun String.toUpperCaseLocal() = this.toUpperCase(Locale.getDefault())

fun String.toLowerCaseLocal() = this.toLowerCase(Locale.getDefault())

fun String.uppercaseLocal(): String = this.toUpperCase(Locale.getDefault())

fun String.capitalizeLocal(): String = this.capitalize(Locale.getDefault())

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
    return DecimalFormat(
        "#,###,###,###",
        DecimalFormatSymbols().apply {
            this.groupingSeparator = ','
        }
    ).format(number)
}

fun hasLockScreen(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure
}