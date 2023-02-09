package com.ivy.exchangeRates.action

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import javax.inject.Inject

//Action to delete items from exchange_rates_override table
class RemoveOverriddenRateAct @Inject constructor(
    private val exchangeRatesOverrideDao: ExchangeRateOverrideDao,
) : Action<RemoveOverriddenRateAct.Input, Unit>() {

    data class Input(
        val baseCurrency: String,
        val currency: String
    )

    override suspend fun action(input: Input) {
        exchangeRatesOverrideDao.deleteByBaseCurrencyAndCurrency(
            baseCurrency = input.baseCurrency,
            currency = input.currency
        )
    }
}