package com.ivy.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap

interface ExchangeProvider {
    suspend fun fetchExchangeRates(baseCurrency: CurrencyCode): ExchangeRatesMap
}