package com.ivy.core.domain.pure.exchange

import arrow.core.*
import arrow.core.computations.option
import com.ivy.data.CurrencyCode
import com.ivy.data.exchange.ExchangeRates

/**
 * @return the successfully exchanged amount or the amount as it was if the rate was missing
 */
@JvmName("exchangeExt")
suspend fun ExchangeRates.exchange(
    from: CurrencyCode,
    to: CurrencyCode,
    amount: Double
): Double = exchange(
    exchangeData = this,
    from = from,
    to = to,
    amount = amount
).getOrElse { amount }

suspend fun exchange(
    exchangeData: ExchangeRates,
    from: CurrencyCode,
    to: CurrencyCode,
    amount: Double,
): Option<Double> = option {
    if (from == to) return@option amount
    if (amount == 0.0) return@option 0.0

    val rate = findRate(
        ratesData = exchangeData,
        from = from,
        to = to,
    ).bind()

    rate * amount
}

suspend fun findRate(
    ratesData: ExchangeRates,
    from: CurrencyCode,
    to: CurrencyCode,
): Option<Double> = option {
    val fromCurrency = from.validateCurrency().bind()
    val toCurrency = to.validateCurrency().bind()

    if (fromCurrency == toCurrency) return@option 1.0

    val rates = ratesData.rates

    when (ratesData.baseCurrency) {
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

private fun String.validateCurrency(): Option<String> {
    return if (this.isNotBlank()) return Some(this) else None
}

fun Double?.validateRate(): Option<Double> {
    val rate = this ?: return None
    //exchange rate which <= 0 is invalid!
    return if (rate > 0) return Some(this) else None
}