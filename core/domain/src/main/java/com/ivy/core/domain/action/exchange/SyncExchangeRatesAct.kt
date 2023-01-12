package com.ivy.core.domain.action.exchange

import com.ivy.common.isNotBlank
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.data.CurrencyCode
import com.ivy.exchange.RemoteExchangeProvider
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val exchangeProvider: RemoteExchangeProvider,
    private val exchangeRateDao: ExchangeRateDao
) : Action<CurrencyCode, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(baseCurrency: CurrencyCode) {
        if (baseCurrency.isNotBlank()) {
            syncExchangeRates(baseCurrency)
        }
    }

    private suspend fun syncExchangeRates(baseCurrency: CurrencyCode) {
        val result = exchangeProvider.fetchExchangeRates(baseCurrency = baseCurrency)
        exchangeRateDao.save(
            result.ratesMap.mapNotNull { (currency, rate) ->
                if (rate > 0.0) {
                    ExchangeRateEntity(
                        baseCurrency = baseCurrency.uppercase(),
                        currency = currency.uppercase(),
                        rate = rate,
                        provider = result.provider
                    )
                } else null
            }
        )
    }
}

