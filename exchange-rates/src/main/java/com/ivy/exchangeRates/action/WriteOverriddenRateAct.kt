package com.ivy.exchangeRates.action

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import javax.inject.Inject

//Action to add items to exchange_rates_override table
class WriteOverriddenRateAct @Inject constructor(
    private val exchangeRatesOverrideDao: ExchangeRateOverrideDao,
) : Action<ExchangeRateOverrideEntity, Unit>() {

    override suspend fun action(input: ExchangeRateOverrideEntity) {
        exchangeRatesOverrideDao.save(
            listOf(input)
        )
    }
}