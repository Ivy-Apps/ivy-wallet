package com.ivy.core.domain.pure

import android.icu.util.Currency
import com.ivy.data.CurrencyCode
import java.util.*


fun getDefaultCurrency(): CurrencyCode =
    (Currency.getInstance(Locale.getDefault()) ?: Currency.getInstance("USD")
    ?: Currency.getInstance("usd") ?: Currency.getAvailableCurrencies().firstOrNull()
    ?: Currency.getInstance("EUR")).currencyCode
