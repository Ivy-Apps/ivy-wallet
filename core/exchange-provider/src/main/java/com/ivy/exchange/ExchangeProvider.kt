package com.ivy.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRates

interface ExchangeProvider {
    suspend fun fetchExchangeRates(baseCurrency: CurrencyCode): ExchangeRates
}