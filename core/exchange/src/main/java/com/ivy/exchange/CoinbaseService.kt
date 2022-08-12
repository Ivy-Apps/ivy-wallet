package com.ivy.exchange

import retrofit2.http.GET
import retrofit2.http.Url

interface CoinbaseService {
    companion object {
        fun exchangeRatesUrl(
            baseCurrencyCode: String
        ): String {
            return "https://api.coinbase.com/v2/exchange-rates?currency=${baseCurrencyCode}"
        }
    }

    @GET
    suspend fun getExchangeRates(
        @Url url: String,
    ): CoinbaseRatesResponse
}