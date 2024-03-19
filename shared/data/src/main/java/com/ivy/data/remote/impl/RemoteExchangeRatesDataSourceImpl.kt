package com.ivy.data.remote.impl

import arrow.core.Either
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.responses.ExchangeRatesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class RemoteExchangeRatesDataSourceImpl @Inject constructor(
    private val ktorClient: dagger.Lazy<HttpClient>,
) : RemoteExchangeRatesDataSource {
    override val urls =
        listOf(
            "https://currency-api.pages.dev/v1/currencies/eur.json",
            "https://currency-api.pages.dev/v1/currencies/eur.min.json",
            "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.min.json",
            "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json",
        )

    override suspend fun fetchEurExchangeRates(url: String): Either<String, ExchangeRatesResponse> =
        Either.catchOrThrow<Exception, ExchangeRatesResponse> {
            ktorClient.get().get(url).body<ExchangeRatesResponse>()
        }.mapLeft { e ->
            e.message ?: "Error fetching exchange rates"
        }
}
