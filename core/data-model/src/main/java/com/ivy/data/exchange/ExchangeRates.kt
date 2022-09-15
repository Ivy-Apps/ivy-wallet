package com.ivy.data.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap

data class ExchangeRates(
    val baseCurrency: CurrencyCode,
    val rates: ExchangeRatesMap
)