package com.ivy.core.domain.functions

import android.icu.util.Currency
import java.util.*


fun getDefaultCurrencyCode(): String =
    (Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")).currencyCode
