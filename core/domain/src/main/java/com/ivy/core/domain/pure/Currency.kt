package com.ivy.core.domain.pure

import android.icu.util.Currency
import com.ivy.data.CurrencyCode
import com.ivy.data.IvyCurrency
import java.util.*


fun getDefaultCurrency(): CurrencyCode =
    (Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")).currencyCode

fun isFiat(currency: CurrencyCode): Boolean = !isCrypto(currency)

fun isCrypto(currency: CurrencyCode): Boolean =
    IvyCurrency.CRYPTO.map { it.code }.contains(currency)