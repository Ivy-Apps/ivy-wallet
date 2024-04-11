package com.ivy.domain.usecase.exchange

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.repository.ExchangeRatesRepository
import timber.log.Timber
import javax.inject.Inject

class SyncExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRatesRepository,
) {
    suspend fun sync(baseCurrency: AssetCode): Either<String, Unit> = either {
        val eurRates = repository.fetchEurExchangeRates().bind()
            .associateBy(ExchangeRate::currency)
            .mapValues { it.value.rate }

        /* At this point we must have non-empty EUR rates
           Now we must convert them to base currency

        "eur": {
            "bgn": 1.955902,
            "usd": 1.062366,
        }
         */
        val eurToBaseRate = eurRates[baseCurrency]
        ensure(eurToBaseRate != null) { "EUR to ${baseCurrency.code} rate not found." }

        val baseCurrencyRates = eurRates.mapNotNull { (target, rate) ->
            either {
                val baseTargetRate = rate.value / eurToBaseRate.value
                ExchangeRate(
                    baseCurrency = baseCurrency,
                    currency = target,
                    rate = PositiveDouble.from(baseTargetRate).bind(),
                    manualOverride = false
                )
            }.getOrNull()
        }.toList()
        Timber.d("Updating exchange rates: $baseCurrencyRates")
        val manuallyOverridden = repository.findAllManuallyOverridden()
            .map { it.identifier() }
            .toSet()
        val newRatesToSave = baseCurrencyRates.mapNotNull { newRate ->
            val hasManualOverride = newRate.identifier() in manuallyOverridden
            // Only save exchange rates that are not overridden
            newRate.takeIf { !hasManualOverride }
        }
        repository.saveManyRates(newRatesToSave)
    }

    private fun ExchangeRate.identifier(): AssetCodeId = AssetCodeId(
        baseCurrency = baseCurrency,
        currency = currency
    )

    data class AssetCodeId(
        val baseCurrency: AssetCode,
        val currency: AssetCode,
    )
}
