package com.ivy.core.action.currency.exchange

import com.ivy.data.ExchangeRates
import com.ivy.exchange.cache.ExchangeRateDao
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.exchangeRatesUpdate
import com.ivy.state.readIvyState
import com.ivy.state.writeIvyState
import javax.inject.Inject

class ExchangeRatesAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) : FPAction<Unit, ExchangeRates>() {
    override suspend fun Unit.compose(): suspend () -> ExchangeRates = {
        readIvyState().exchangeRates ?: retrieveRatesFromDB()
    }

    private suspend fun retrieveRatesFromDB(): ExchangeRates =
        exchangeRateDao::findAll thenMap {
            (it.currency to it.rate)
        } then { it.toMap() } thenInvokeAfter {
            writeIvyState(exchangeRatesUpdate(newExchangeRates = it))
            it
        }

}