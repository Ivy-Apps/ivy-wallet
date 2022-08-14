package com.ivy.core.action.currency.exchange

import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.data.CurrencyCode
import com.ivy.exchange.ExchangeProvider
import com.ivy.exchange.cache.ExchangeRateDao
import com.ivy.exchange.cache.ExchangeRateEntity
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.fixUnit
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.exchangeRatesUpdate
import com.ivy.state.writeIvyState
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val baseCurrencyAct: BaseCurrencyAct,
    private val exchangeProvider: ExchangeProvider,
    private val exchangeRateDao: ExchangeRateDao
) : FPAction<Unit, Unit>() {
    override suspend fun Unit.compose(): suspend () -> Unit =
        (baseCurrencyAct then ::syncExchangeRates).fixUnit()

    private suspend fun syncExchangeRates(baseCurrency: CurrencyCode) =
        baseCurrency asParamTo exchangeProvider::fetchExchangeRates then {
            writeIvyState(exchangeRatesUpdate(newExchangeRates = it))
            it.map { (currency, rate) ->
                ExchangeRateEntity(
                    baseCurrency = baseCurrency,
                    currency = currency,
                    rate = rate
                )
            }
        } thenInvokeAfter exchangeRateDao::save
}

