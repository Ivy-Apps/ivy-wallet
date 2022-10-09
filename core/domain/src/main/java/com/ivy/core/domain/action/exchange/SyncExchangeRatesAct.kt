package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.data.CurrencyCode
import com.ivy.exchange.RemoteExchangeProvider
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val exchangeProvider: RemoteExchangeProvider,
    private val exchangeRateDao: ExchangeRateDao
) : Action<String, Unit>() {
    override suspend fun String.willDo() {
        syncExchangeRates(this)
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

