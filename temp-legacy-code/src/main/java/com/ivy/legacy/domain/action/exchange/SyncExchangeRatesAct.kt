package com.ivy.legacy.domain.action.exchange

import androidx.annotation.Keep
import com.ivy.frp.action.Action
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.entity.ExchangeRateEntity
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val writeExchangeRatesDao: WriteExchangeRatesDao,
    private val ktorClient: Lazy<HttpClient>,
) : Action<SyncExchangeRatesAct.Input, Unit>() {
    data class Input(
        val baseCurrency: String,
    )

    companion object {
        private val URLS = listOf(
            "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json",
            "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.min.json",
            "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.min.json",
            "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.json",
        )
    }

    override suspend fun Input.willDo() = io {
        try {
            sync(baseCurrency)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun sync(baseCurrency: String) {
        if (baseCurrency.isBlank()) return
        val baseCurrencyLower = baseCurrency.lowercase()

        var eurRates: Map<String, Double> = emptyMap()
        for (url in URLS) {
            eurRates = fetchEurRates(url)
            if (eurRates.isNotEmpty()) break
        }
        if (eurRates.isEmpty()) return

        // At this point we must have non-empty EUR rates
        // Now we must convert them to base currency
        /*
            "eur": {
                "bgn": 1.955902,
                "usd": 1.062366,
            }
         */
        val eurBaseCurr = eurRates[baseCurrencyLower]
            ?.takeIf { it > 0 } ?: return

        val rateEntities = eurRates.mapNotNull { (target, rate) ->
            try {
                val baseTargetRate = rate / eurBaseCurr
                ExchangeRateEntity(
                    baseCurrency = baseCurrency.uppercase(),
                    currency = target.uppercase(),
                    rate = baseTargetRate
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }.toList()
        Timber.d("Updating exchange rates: $rateEntities")
        rateEntities.map { newRate ->
            val manualOverride = exchangeRatesDao.findByBaseCurrencyAndCurrency(
                baseCurrency = newRate.baseCurrency,
                currency = newRate.currency
            )?.manualOverride ?: false

            if (!manualOverride && newRate.rate > 0.0) {
                // save only the once that aren't overridden
                writeExchangeRatesDao.save(newRate)
            }
        }
    }

    private suspend fun fetchEurRates(url: String): Map<String, Double> {
        return try {
            val client = ktorClient.get()
            client.get(url).body<Response>().eur
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    @Keep
    @Serializable
    data class Response(
        val eur: Map<String, Double>
    )
}
