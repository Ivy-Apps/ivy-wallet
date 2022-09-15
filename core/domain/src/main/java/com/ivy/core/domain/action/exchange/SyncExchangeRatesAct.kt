package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.data.CurrencyCode
import com.ivy.exchange.ExchangeProvider
import com.ivy.exchange.cache.ExchangeRateDao
import com.ivy.exchange.cache.ExchangeRateEntity
import com.ivy.frp.action.Action
import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val exchangeProvider: ExchangeProvider,
    private val exchangeRateDao: ExchangeRateDao
) : Action<Unit, Unit>() {
    override suspend fun Unit.willDo() {
        val baseCurrency = baseCurrencyFlow().first()
        if (baseCurrency == "") willDo() else syncExchangeRates(baseCurrency)
    }

    private suspend fun syncExchangeRates(baseCurrency: CurrencyCode) =
        baseCurrency asParamTo exchangeProvider::fetchExchangeRates then {
            it.map { (currency, rate) ->
                ExchangeRateEntity(
                    baseCurrency = baseCurrency,
                    currency = currency,
                    rate = rate
                )
            }
        } thenInvokeAfter exchangeRateDao::save
}

