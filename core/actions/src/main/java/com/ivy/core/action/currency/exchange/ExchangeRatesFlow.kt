package com.ivy.core.action.currency.exchange

import com.ivy.core.action.SharedFlowAction
import com.ivy.data.ExchangeRates
import com.ivy.exchange.cache.ExchangeRateDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesFlow @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) : SharedFlowAction<ExchangeRates>() {
    override suspend fun initialValue(): ExchangeRates = emptyMap()

    override suspend fun createFlow(): Flow<ExchangeRates> = exchangeRateDao.findAll()
        .map { entities ->
            entities.associate { it.currency to it.rate }
        }.flowOn(Dispatchers.Default)
}