package com.ivy.core.domain.algorithm.calc

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.data.CurrencyCode
import com.ivy.data.exchange.ExchangeRates
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val db: IvyWalletCoreDb,
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
                    db.ratesDao().findAll(baseCurrency),
                    db.ratesDao().findAllOverrides(baseCurrency)
                ) { rates, overrides ->
                    val finalRates = mutableMapOf<CurrencyCode, Double>()
                    // Automatic (remotely fetched) rates
                    rates.forEach { entry ->
                        finalRates[entry.currency] = entry.rate
                    }
                    // Manually overridden or custom added rates
                    overrides.forEach { entry ->
                        finalRates[entry.currency] = entry.rate
                    }
                    ExchangeRates(
                        baseCurrency = baseCurrency,
                        rates = finalRates,
                    )
                }
            }
        }
}