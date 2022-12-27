package com.ivy.exchange.fawazahmed0

import com.ivy.data.CurrencyCode
import com.ivy.data.exchange.ExchangeProvider
import com.ivy.exchange.RemoteExchangeProvider
import com.ivy.network.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import javax.inject.Inject

class Fawazahmed0ExchangeProvider @Inject constructor(

) : RemoteExchangeProvider {
    companion object {
        private val FALLBACK_URLS = listOf(
            "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json",
            "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.min.json",
            "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.min.json",
            "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.json",
        )
    }

    override suspend fun fetchExchangeRates(baseCurrency: CurrencyCode): RemoteExchangeProvider.Result {
        if (baseCurrency.isBlank()) return failure()

        var eurRates: Map<String, Double> = emptyMap()
        for (url in FALLBACK_URLS) {
            eurRates = fetchEurBaseRates(url)
            if (eurRates.isNotEmpty()) break // rates fetched successfully, stop!
        }
        if (eurRates.isEmpty()) return failure() // empty rates = no rates = failure

        // At this point we must have non-empty EUR rates map
        // Now we must convert them to base currency
        /*
            "eur": {
                "bgn": 1.955902,
                "usd": 1.062366,
            }
         */
        // the API works with lowercase currency codes
        val baseCurrencyLower = baseCurrency.lowercase()
        val eurBaseCurrRateNonZero = eurRates[baseCurrencyLower]
            ?.takeIf { it > 0.0 } ?: return failure()

        val rates = eurRates.mapNotNull { (target, eurTargetRate) ->
            try {
                if (eurTargetRate > 0.0) {
                    val baseCurrencyTargetRate = eurTargetRate / eurBaseCurrRateNonZero
                    target.uppercase() to baseCurrencyTargetRate
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        return RemoteExchangeProvider.Result(
            ratesMap = rates.toMap(),
            provider = ExchangeProvider.Fawazahmed0
        )
    }

    private suspend fun fetchEurBaseRates(url: String): Map<String, Double> {
        return try {
            ktorClient().get(url).body<Fawazahmed0Response>().eur
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    private fun failure() = RemoteExchangeProvider.Result(
        ratesMap = emptyMap(),
        provider = ExchangeProvider.Fawazahmed0
    )
}