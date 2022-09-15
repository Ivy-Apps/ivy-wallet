package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.data.CurrencyCode
import com.ivy.exchange.RemoteExchangeProvider
import com.ivy.frp.action.Action
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val exchangeProvider: RemoteExchangeProvider,
    private val exchangeRateDao: ExchangeRateDao
) : Action<Unit, Unit>() {
    override suspend fun Unit.willDo() {
        val baseCurrency = baseCurrencyFlow().first()
        if (baseCurrency == "") willDo() else syncExchangeRates(baseCurrency)
    }

    private suspend fun syncExchangeRates(baseCurrency: CurrencyCode) {
        val result = exchangeProvider.fetchExchangeRates(baseCurrency = baseCurrency)
        exchangeRateDao.save(
            result.ratesMap.map { (currency, rate) ->
                ExchangeRateEntity(
                    baseCurrency = baseCurrency,
                    currency = currency,
                    rate = rate,
                    provider = result.provider
                )
            }
        )
    }
}

