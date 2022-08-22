package com.ivy.exchange.coinbase

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRates
import com.ivy.exchange.ExchangeProvider
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.mapError
import com.ivy.frp.monad.mapSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import javax.inject.Inject

class CoinbaseExchangeProvider @Inject constructor(
    private val coinbaseService: CoinbaseService,
) : ExchangeProvider {
    override suspend fun fetchExchangeRates(
        baseCurrency: CurrencyCode
    ): ExchangeRates = tryOp(
        operation = CoinbaseService.exchangeRatesUrl(
            baseCurrency = baseCurrency
        ) asParamTo coinbaseService::getExchangeRates
    ) mapSuccess {
        it.data.rates
    } mapError {
        emptyMap<CurrencyCode, Double>()
    } thenInvokeAfter {
        when (it) {
            is Res.Ok -> it.data
            is Res.Err -> it.error
        }
    }


}