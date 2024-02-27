package com.ivy.data.repository

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl

interface ExchangeRatesRepository {

    val urls: List<String>
    fun fetchExchangeRates(url: String) : RemoteExchangeRatesDataSourceImpl.ExchangeRatesResponse

    fun save(value: ExchangeRateEntity)

    fun saveMany(value: List<ExchangeRateEntity>)

    fun save(value: ExchangeRate)

    fun saveMany(value: ExchangeRate)

    fun deleteAll()

    fun findAll(): List<ExchangeRateEntity>
    fun findByBaseCurrencyAndCurrency(baseCurrency: String, currency: String) : ExchangeRateEntity

}