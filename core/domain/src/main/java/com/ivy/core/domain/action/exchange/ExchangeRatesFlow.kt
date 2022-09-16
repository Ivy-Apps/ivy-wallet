package com.ivy.core.domain.action.exchange

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.data.exchange.ExchangeRatesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val exchangeRateDao: ExchangeRateDao,
    private val exchangeRateOverrideDao: ExchangeRateOverrideDao,
) : SharedFlowAction<ExchangeRatesData>() {
    override fun initialValue(): ExchangeRatesData = ExchangeRatesData(
        baseCurrency = "",
        rates = emptyMap()
    )

    @OptIn(FlowPreview::class)
    override fun createFlow(): Flow<ExchangeRatesData> =
        baseCurrencyFlow().flatMapMerge { baseCurrency ->
            combine(
                exchangeRateDao.findAllByBaseCurrency(baseCurrency),
                exchangeRateOverrideDao.findAllByBaseCurrency(baseCurrency)
            ) { rateEntities, ratesOverride ->
                val ratesMap = rateEntities
                    .filter { it.baseCurrency == baseCurrency }
                    .associate { it.currency to it.rate }
                    .toMutableMap()

                ratesOverride.filter { it.baseCurrency == baseCurrency }
                    .onEach {
                        // override automatic rates by manually set ones
                        ratesMap[it.currency] = it.rate
                    }

                ExchangeRatesData(
                    baseCurrency = baseCurrency,
                    rates = ratesMap,
                )
            }
        }.flowOn(Dispatchers.Default)
}