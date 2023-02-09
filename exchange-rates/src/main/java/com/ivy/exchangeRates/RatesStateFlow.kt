package com.ivy.exchangeRates

import com.ivy.exchangeRates.data.RateUi
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

//returns flow of RatesState which contains manual and automatic exchangeRates list
@Singleton
class RatesStateFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val ratesDao: RatesDao
) : SharedFlowAction<RatesState>() {
    override fun initialValue(): RatesState = RatesState(
        baseCurrency = "",
        manual = emptyList(),
        automatic = emptyList()
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(): Flow<RatesState> = baseCurrencyFlow()
        .flatMapLatest { baseCurrency ->
            if (baseCurrency.isBlank()) {
                flowOf(initialValue())
            } else {
                combine(
                    ratesDao.findAll(baseCurrency),
                    ratesDao.findAllOverrides(baseCurrency)
                ) { rates, overrides ->
                    //converting list to set to improve efficiency of contains()
                    val manual = overrides.map { it.currency }.toSet()
                    RatesState(
                        baseCurrency = baseCurrency,
                        manual = overrides.map {
                            toRateUi(baseCurrency, it)
                        },
                        automatic = rates.filter {
                            !manual.contains(it.currency)
                        }.map {
                            toRateUi(baseCurrency, it)
                        }
                    )
                }
            }
        }

    private fun toRateUi(baseCurrency: String, rate: Rate) = RateUi(
        from = baseCurrency,
        to = rate.currency,
        rate = rate.rate
    )

}