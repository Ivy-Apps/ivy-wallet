package com.ivy.data.remote

import arrow.core.Either
import com.ivy.data.remote.responses.ExchangeRatesResponse

interface RemoteExchangeRatesDataSource {
    val urls: List<String>

    suspend fun fetchEurExchangeRates(url: String): Either<String, ExchangeRatesResponse>
}
