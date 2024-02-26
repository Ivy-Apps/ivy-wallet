package com.ivy.data.repository

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl

interface ExchangeRatesRepository {

    val urls: List<String>
    fun fetchExchangeRates(url: String) : RemoteExchangeRatesDataSourceImpl.ExchangeRatesResponse

    fun findByBaseCurrencyAndCurrency(baseCurrency: String, currency: String) : ExchangeRateEntity
    fun save(exchangeRate: ExchangeRateEntity)

    fun readExchangeRates()
}