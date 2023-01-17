package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.RatesDao
import com.ivy.data.exchange.ExchangeRates
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val ratesDao: RatesDao,
) : SharedFlowAction<ExchangeRates>() {
    override fun initialValue() = ExchangeRates(
        baseCurrency = "",
        rates = emptyMap()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(): Flow<ExchangeRates> = baseCurrencyFlow()
        .flatMapLatest { baseCurrency ->
            if (baseCurrency.isBlank()) {
                flowOf(initialValue())
            } else {
                combine(
                    ratesDao.findAll(baseCurrency),
                    ratesDao.findAllOverrides(baseCurrency)
                ) { rates, overrides ->
                    TODO()
                }
            }
        }

}