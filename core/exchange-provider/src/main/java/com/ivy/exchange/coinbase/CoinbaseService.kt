package com.ivy.exchange.coinbase

import com.ivy.data.CurrencyCode
import retrofit2.http.GET
import retrofit2.http.Url

interface CoinbaseService {
    companion object {
        fun exchangeRatesUrl(
            baseCurrency: CurrencyCode
        ): String {
            return "https://api.coinbase.com/v2/exchange-rates?currency=${baseCurrency}"
        }
    }

    @GET
    suspend fun getExchangeRates(
        @Url url: String,
    ): CoinbaseRatesResponse
}