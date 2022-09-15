package com.ivy.core.domain.action.currency.exchange

import com.ivy.data.ExchangeRatesMap
import com.ivy.exchange.cache.ExchangeRateDao
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.exchangeRatesUpdate
import com.ivy.state.readIvyState
import com.ivy.state.writeIvyState
import javax.inject.Inject

@Deprecated(
    message = "migrating to flows",
    replaceWith = ReplaceWith("ExchangeRatesFlow")
)
class ExchangeRatesAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) : FPAction<Unit, ExchangeRatesMap>() {
    override suspend fun Unit.compose(): suspend () -> ExchangeRatesMap = {
        readIvyState().exchangeRates ?: retrieveRatesFromDB()
    }

    private suspend fun retrieveRatesFromDB(): ExchangeRatesMap =
        exchangeRateDao::findAllSuspend thenMap {
            (it.currency to it.rate)
        } then { it.toMap() } thenInvokeAfter {
            writeIvyState(exchangeRatesUpdate(newExchangeRates = it))
            it
        }

}