package com.ivy.data.repository.impl

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl
import com.ivy.data.repository.ExchangeRatesRepository
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor() : ExchangeRatesRepository {

    override val urls = listOf(
        "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json",
        "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.min.json",
        "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.min.json",
        "https://raw.githubusercontent.com/fawazahmed0/currency-api/1/latest/currencies/eur.json",
    )
    override fun fetchExchangeRates(url: String) : RemoteExchangeRatesDataSourceImpl.ExchangeRatesResponse{
        TODO("Not yet implemented")
    }

    override fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity {
        TODO("Not yet implemented")
    }

    override fun save(value: ExchangeRateEntity) {
        TODO("Not yet implemented")
    }

    override fun save(value: ExchangeRate) {
        TODO("Not yet implemented")
    }

    override fun saveMany(value: List<ExchangeRateEntity>) {
        TODO("Not yet implemented")
    }

    override fun saveMany(value: ExchangeRate) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<ExchangeRateEntity> {
        TODO("Not yet implemented")
    }
}