package com.ivy.core.domain.action.currency.exchange

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import com.ivy.core.domain.action.currency.BaseCurrencyFlow
import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap
import com.ivy.frp.Pure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ExchangeFlow @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : com.ivy.core.domain.action.FlowAction<ExchangeFlow.Input, Option<Double>>() {
    data class Input(
        val from: CurrencyCode,
        val to: CurrencyCode,
        val amount: Double
    )

    override fun Input.createFlow(): Flow<Option<Double>> =
        combine(exchangeRatesFlow(), baseCurrencyFlow()) { rates, baseCurrency ->
            exchange(rates = rates, baseCurrency = baseCurrency)
        }.flowOn(Dispatchers.Default)

    private suspend fun Input.exchange(
        rates: ExchangeRatesMap,
        baseCurrency: CurrencyCode,
    ) = option {
        if (from == to) return@option amount
        if (amount == 0.0) return@option 0.0

        val rate = findRate(
            rates = rates,
            from = from,
            to = to,
            baseCurrency = baseCurrency,
        ).bind()

        rate * amount
    }

    private suspend fun findRate(
        rates: ExchangeRatesMap,
        from: CurrencyCode,
        to: CurrencyCode,
        baseCurrency: CurrencyCode,
    ): Option<Double> = option {
        val fromCurrency = from.validateCurrency().bind()
        val toCurrency = to.validateCurrency().bind()

        if (fromCurrency == toCurrency) return@option 1.0

        when (baseCurrency) {
            fromCurrency -> {
                // exchange from base currency to other currency
                //w e need the rate from baseCurrency to toCurrency
                rates[toCurrency].validateRate().bind()
                //toAmount = fromAmount * rateFromTo
            }
            toCurrency -> {
                // exchange from other currency to base currency
                // we'll get the rate to

                /*
                Example: fromA = 10 fromC = EUR; toC = BGN
                rateToFrom = rate (BGN EUR) ~= 0.51

                Formula: (10 EUR / 0.51 ~= 19.67)
                    fromAmount / rateToFrom

                EXPECTED: 10 EUR ~= 19.67 BGN
                 */
                1.0 / rates[fromCurrency].validateRate().bind()
            }
            else -> {
                //exchange from other currency to other currency
                //that's the only possible case left because we already checked "fromCurrency == toCurrency"

                val rateBaseFrom = rates[fromCurrency].validateRate().bind()
                val rateBaseTo = rates[toCurrency].validateRate().bind()

                //Convert: toBaseCurrency -> toToCurrency
                val rateBase = 1 / rateBaseFrom
                rateBase * rateBaseTo
            }
        }
    }

    @Pure
    private fun String.validateCurrency(): Option<String> {
        return if (this.isNotBlank()) return Some(this) else None
    }

    @Pure
    fun Double?.validateRate(): Option<Double> {
        val rate = this ?: return None
        //exchange rate which <= 0 is invalid!
        return if (rate > 0) return Some(this) else None
    }
}