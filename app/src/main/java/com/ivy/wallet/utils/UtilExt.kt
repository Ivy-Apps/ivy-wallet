package com.ivy.wallet.utils

import android.app.KeyguardManager
import android.content.Context
import android.icu.util.Currency
import java.util.*
import kotlin.math.abs
import kotlin.math.log10

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

fun String.nullifyEmpty() = this.ifBlank { null }

fun getDefaultFIATCurrency(): Currency =
    Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")

fun String.toUpperCaseLocal() = this.uppercase(Locale.getDefault())

fun String.toLowerCaseLocal() = this.lowercase(Locale.getDefault())

fun String.uppercaseLocal(): String = this.uppercase(Locale.getDefault())

fun String.capitalizeLocal(): String = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.capitalizeLocal() }
}

fun hasLockScreen(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure
}