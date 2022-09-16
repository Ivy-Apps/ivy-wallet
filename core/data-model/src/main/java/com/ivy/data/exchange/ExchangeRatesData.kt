package com.ivy.data.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap

data class ExchangeRatesData(
    val baseCurrency: CurrencyCode,
    val rates: ExchangeRatesMap
)