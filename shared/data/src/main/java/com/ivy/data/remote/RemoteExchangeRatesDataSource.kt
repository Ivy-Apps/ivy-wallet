package com.ivy.data.remote

import arrow.core.Either
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl

interface RemoteExchangeRatesDataSource {
    suspend fun fetchEurExchangeRates(url: String) : Either<String, RemoteExchangeRatesDataSourceImpl.ExchangeRatesResponse>
}