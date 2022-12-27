package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.currency.OldExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface OldExchangeService {
    companion object {
        fun exchangeRatesUrl(
            baseCurrencyCode: String
        ): String {
            return "https://api.exchangeold.com/exchange-rates?currency=${baseCurrencyCode}"
        }
    }

    @GET
    suspend fun getExchangeRates(
        @Url url: String,
    ): OldExchangeRateResponse
}