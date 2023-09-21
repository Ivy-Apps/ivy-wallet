package com.ivy.legacy.utils

import android.app.KeyguardManager
import android.content.Context
import android.icu.util.Currency
import java.util.Locale
import java.util.Random

fun <T> MutableList<T>.swap(fromIndex: Int, toIndex: Int) {
    val from = this[fromIndex]
    val to = this[toIndex]

    this[fromIndex] = to
    this[toIndex] = from
}

fun numberBetween(min: Double, max: Double): Double {
    return Random().nextDouble() * (max - min) + min
}

fun getDefaultFIATCurrency(): Currency =
    Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")

fun String.toUpperCaseLocal() = this.uppercase(Locale.getDefault())

fun String.toLowerCaseLocal() = this.lowercase(Locale.getDefault())

fun String.uppercaseLocal(): String = this.uppercase(Locale.getDefault())

fun String.capitalizeLocal(): String = this.replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(
            Locale.getDefault()
        )
    } else {
        it.toString()
    }
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.capitalizeLocal() }
}

fun hasLockScreen(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isDeviceSecure
}