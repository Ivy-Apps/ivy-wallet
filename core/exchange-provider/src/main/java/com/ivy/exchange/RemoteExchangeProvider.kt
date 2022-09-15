package com.ivy.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap

interface RemoteExchangeProvider {
    suspend fun fetchExchangeRates(baseCurrency: CurrencyCode): Result

    data class Result(
        val ratesMap: ExchangeRatesMap,
        val provider: com.ivy.data.exchange.ExchangeProvider
    )
}