package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.currency.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface ExchangeRatesService {
    @GET
    suspend fun getExchangeRates(
        @Url url: String,
    ): ExchangeRatesResponse
}