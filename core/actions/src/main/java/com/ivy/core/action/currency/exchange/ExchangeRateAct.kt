package com.ivy.core.action.currency.exchange

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.data.CurrencyCode
import com.ivy.frp.Pure
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class ExchangeRateAct @Inject constructor(
    private val exchangeRatesAct: ExchangeRatesAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<ExchangeRateAct.Input, Option<Double>>() {
    data class Input(
        val from: CurrencyCode,
        val to: CurrencyCode
    )

    override suspend fun Input.compose(): suspend () -> Option<Double> = {
        findRate()
    }

    private suspend fun Input.findRate(): Option<Double> = option {
        val fromCurrency = from.validateCurrency().bind()
        val toCurrency = to.validateCurrency().bind()

        if (fromCurrency == toCurrency) return@option 1.0

        val rates = exchangeRatesAct(Unit)

        when (baseCurrencyAct(Unit)) {
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