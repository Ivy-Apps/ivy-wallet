package com.ivy.data.remote.impl

import arrow.core.Either
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class RemoteExchangeRatesDataSourceImpl @Inject constructor(
    private val ktorClient: Lazy<HttpClient>
) : RemoteExchangeRatesDataSource {

    private val urls = listOf(
        "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json",
        "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.min.json",
        "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.min.json",
        "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.json",
    )

    data class ExchangeRatesResponse(
        val date: String,
        val rates: Map<String, Double>
    )
    override suspend fun fetchEurExchangeRates(url: String): Either<String, ExchangeRatesResponse> =
        Either.catchOrThrow<Exception, ExchangeRatesResponse> {
            ktorClient.value.get(url).body<ExchangeRatesResponse>()
        }.mapLeft { e ->
            e.message ?: "Error fetching exchange rates"
        }

}