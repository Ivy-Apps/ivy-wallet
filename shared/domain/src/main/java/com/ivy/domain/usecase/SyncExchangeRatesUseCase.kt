package com.ivy.domain.usecase

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.repository.ExchangeRatesRepository
import timber.log.Timber
import javax.inject.Inject

class SyncExchangeRatesUseCase
    @Inject
    constructor(
        private val repository: ExchangeRatesRepository,
    ) {
        suspend fun sync(baseCurrency: String) {
            val baseCurrencyLower = baseCurrency.lowercase()

            val exchangeRatesResponse = repository.fetchExchangeRates() ?: return

            /* At this point we must have non-empty EUR rates
               Now we must convert them to base currency

            "eur": {
                "bgn": 1.955902,
                "usd": 1.062366,
            }
             */
            val eurBaseCurr =
                exchangeRatesResponse.rates[baseCurrencyLower]
                    ?.takeIf { it > 0 } ?: return

            val rateEntities =
                exchangeRatesResponse.rates.mapNotNull { (target, rate) ->
                    try {
                        val baseTargetRate = rate / eurBaseCurr
                        ExchangeRateEntity(
                            baseCurrency = baseCurrency.uppercase(),
                            currency = target.uppercase(),
                            rate = baseTargetRate,
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }.toList()
            Timber.d("Updating exchange rates: $rateEntities")
            rateEntities.map { newRate ->
                val manualOverride =
                    repository.findByBaseCurrencyAndCurrency(
                        baseCurrency = newRate.baseCurrency,
                        currency = newRate.currency,
                    )?.manualOverride ?: false

                if (!manualOverride && newRate.rate > 0.0) {
                    // Only save exchange rates that are not overridden
                    repository.save(newRate)
                }
            }
        }
    }
