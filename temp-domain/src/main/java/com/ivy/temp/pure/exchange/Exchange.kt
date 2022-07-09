package com.ivy.wallet.domain.pure.exchange

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import arrow.core.toOption
import com.ivy.exchange.ExchangeData
import com.ivy.exchange.ExchangeRate
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import java.math.BigDecimal


@Pure
suspend fun exchange(
    data: ExchangeData,
    amount: BigDecimal,

    @SideEffect
    getExchangeRate: suspend (baseCurrency: String, toCurrency: String) -> ExchangeRate?,
): Option<BigDecimal> = option {
    if (amount == BigDecimal.ZERO) {
        return@option BigDecimal.ZERO
    }

    val fromCurrency = data.fromCurrency.bind().validateCurrency().bind()
    val toCurrency = data.toCurrency.validateCurrency().bind()

    if (fromCurrency == toCurrency) {
        return@option amount
    }

    when (val baseCurrency = data.baseCurrency.validateCurrency().bind()) {
        fromCurrency -> {
            //exchange from base currency to other currency
            //we need the rate from baseCurrency to toCurrency
            val rateFromTo = validExchangeRate(
                baseCurrency = fromCurrency, //fromCurrency = baseCurrency
                toCurrency = toCurrency,
                retrieveExchangeRate = getExchangeRate
            ).bind()

            //toAmount = fromAmount * rateFromTo
            amount * rateFromTo
        }
        toCurrency -> {
            //exchange from other currency to base currency
            //we'll get the rate to

            val rateToFrom = validExchangeRate(
                baseCurrency = toCurrency, //toCurrency = baseCurrency
                toCurrency = fromCurrency,
                retrieveExchangeRate = getExchangeRate
            ).bind()

            /*
            Example: fromA = 10 fromC = EUR; toC = BGN
            rateToFrom = rate (BGN EUR) ~= 0.51

            Formula: (10 EUR / 0.51 ~= 19.67)
                fromAmount / rateToFrom

            EXPECTED: 10 EUR ~= 19.67 BGN
             */
            amount / rateToFrom
        }
        else -> {
            //exchange from other currency to other currency
            //that's the only possible case left because we already checked "fromCurrency == toCurrency"

            val rateBaseFrom = validExchangeRate(
                baseCurrency = baseCurrency,
                toCurrency = fromCurrency,
                retrieveExchangeRate = getExchangeRate
            ).bind()

            val rateBaseTo = validExchangeRate(
                baseCurrency = baseCurrency,
                toCurrency = toCurrency,
                retrieveExchangeRate = getExchangeRate
            ).bind()

            //Convert: toBaseCurrency -> toToCurrency
            val amountBaseCurrency = amount / rateBaseFrom
            amountBaseCurrency * rateBaseTo
        }
    }
}

@Pure
private fun String.validateCurrency(): Option<String> {
    return if (this.isNotBlank()) return Some(this) else None
}

@Pure
suspend fun validExchangeRate(
    baseCurrency: String,
    toCurrency: String,
    retrieveExchangeRate: suspend (baseCurrency: String, toCurrency: String) -> ExchangeRate?,
): Option<BigDecimal> = option {
    retrieveExchangeRate(
        baseCurrency, toCurrency
    ).toOption().bind()
        .validateRate().bind()
}

@Pure
fun ExchangeRate.validateRate(): Option<BigDecimal> {
    //exchange rate which <= 0 is invalid!
    return if (rate > 0) return Some(rate.toBigDecimal()) else None
}