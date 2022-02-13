package com.ivy.wallet.functional

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import arrow.core.toOption
import com.ivy.wallet.functional.core.Pure
import com.ivy.wallet.functional.core.Total
import com.ivy.wallet.model.entity.ExchangeRate
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import java.math.BigDecimal

@Total
suspend fun exchangeToBaseCurrency(
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: Option<String>,
    fromCurrencyCode: Option<String>,
    fromAmount: BigDecimal,
): Option<BigDecimal> {
    return exchange(
        exchangeRateDao = exchangeRateDao,
        baseCurrencyCode = baseCurrencyCode,
        fromCurrencyCode = fromCurrencyCode,
        fromAmount = fromAmount,
        toCurrencyCode = baseCurrencyCode,
    )
}

@Total
suspend fun exchange(
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: Option<String>,
    fromCurrencyCode: Option<String>,
    fromAmount: BigDecimal,
    toCurrencyCode: Option<String>,
): Option<BigDecimal> {
    return exchange(
        baseCurrencyCode = baseCurrencyCode,
        fromCurrencyCode = fromCurrencyCode,
        fromAmount = fromAmount,
        toCurrencyCode = toCurrencyCode,
        retrieveExchangeRate = exchangeRateDao::findByBaseCurrencyAndCurrency
    )
}

@Total
suspend fun exchange(
    baseCurrencyCode: Option<String>,
    fromCurrencyCode: Option<String>,
    fromAmount: BigDecimal,
    toCurrencyCode: Option<String>,
    retrieveExchangeRate: suspend (baseCurrency: String, toCurrency: String) -> ExchangeRate?,
): Option<BigDecimal> = option {
    if (fromAmount == BigDecimal.ZERO) {
        return@option BigDecimal.ZERO
    }

    val fromCurrency = fromCurrencyCode.bind()
    val toCurrency = toCurrencyCode.bind()

    if (fromCurrency == toCurrency) {
        return@option fromAmount
    }

    when (val baseCurrency = baseCurrencyCode.bind()) {
        fromCurrency -> {
            //exchange from base currency to other currency
            //we need the rate from baseCurrency to toCurrency
            val rateFromTo = validExchangeRate(
                baseCurrency = fromCurrency, //fromCurrency = baseCurrency
                toCurrency = toCurrency,
                retrieveExchangeRate = retrieveExchangeRate
            ).bind()

            //toAmount = fromAmount * rateFromTo
            fromAmount * rateFromTo
        }
        toCurrency -> {
            //exchange from other currency to base currency
            //we'll get the rate to

            val rateToFrom = validExchangeRate(
                baseCurrency = toCurrency, //toCurrency = baseCurrency
                toCurrency = fromCurrency,
                retrieveExchangeRate = retrieveExchangeRate
            ).bind()

            /*
            Example: fromA = 10 fromC = EUR; toC = BGN
            rateToFrom = rate (BGN EUR) ~= 0.51

            Formula: (10 EUR / 0.51 ~= 19.67)
                fromAmount / rateToFrom

            EXPECTED: 10 EUR ~= 19.67 BGN
             */
            fromAmount / rateToFrom
        }
        else -> {
            //exchange from other currency to other currency
            //that's the only possible case left because we already checked "fromCurrency == toCurrency"

            val rateBaseFrom = validExchangeRate(
                baseCurrency = baseCurrency,
                toCurrency = fromCurrency,
                retrieveExchangeRate = retrieveExchangeRate
            ).bind()

            val rateBaseTo = validExchangeRate(
                baseCurrency = baseCurrency,
                toCurrency = toCurrency,
                retrieveExchangeRate = retrieveExchangeRate
            ).bind()

            //Convert: toBaseCurrency -> toToCurrency
            val amountBaseCurrency = fromAmount / rateBaseFrom
            amountBaseCurrency * rateBaseTo
        }
    }
}

@Total
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