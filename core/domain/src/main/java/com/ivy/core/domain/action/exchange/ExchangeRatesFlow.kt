package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.data.exchange.ExchangeRates
import com.ivy.exchange.cache.ExchangeRateDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val exchangeRateDao: ExchangeRateDao,
) : SharedFlowAction<ExchangeRates>() {
    override fun initialValue(): ExchangeRates = ExchangeRates(
        baseCurrency = "",
        rates = emptyMap()
    )

    override fun createFlow(): Flow<ExchangeRates> = combine(
        baseCurrencyFlow(), exchangeRateDao.findAll()
    ) { baseCurrency, rates ->
        val ratesMap = rates
            .filter { it.baseCurrency == baseCurrency }
            .associate { it.currency to it.rate }

        ExchangeRates(
            baseCurrency = baseCurrency,
            rates = ratesMap,
        )
    }.flowOn(Dispatchers.Default)

}