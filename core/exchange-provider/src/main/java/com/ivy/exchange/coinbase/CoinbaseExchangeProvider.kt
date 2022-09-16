package com.ivy.exchange.coinbase

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap
import com.ivy.data.exchange.ExchangeProvider
import com.ivy.exchange.RemoteExchangeProvider
import com.ivy.network.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class CoinbaseExchangeProvider @Inject constructor() : RemoteExchangeProvider {
    override suspend fun fetchExchangeRates(
        baseCurrency: CurrencyCode
    ): RemoteExchangeProvider.Result = RemoteExchangeProvider.Result(
        ratesMap = fetchRates(baseCurrency),
        provider = ExchangeProvider.Coinbase
    )

    private suspend fun fetchRates(baseCurrency: CurrencyCode): ExchangeRatesMap {
        val response = ktorClient().get("https://api.coinbase.com/v2/exchange-rates") {
            parameter("currency", baseCurrency)
        }

        return if (response.status.isSuccess()) {
            response.body<CoinbaseRatesResponse>().data.rates
        } else {
            // error
            emptyMap()
        }
    }


}