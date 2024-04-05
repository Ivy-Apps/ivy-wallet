package com.ivy.data.repository

import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.remote.responses.ExchangeRatesResponse
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    suspend fun fetchExchangeRates(): ExchangeRatesResponse?

    suspend fun save(value: ExchangeRateEntity)

    suspend fun saveManyEntities(values: List<ExchangeRateEntity>)

    suspend fun save(value: com.ivy.data.model.ExchangeRate)

    suspend fun saveManyRates(values: List<com.ivy.data.model.ExchangeRate>)

    suspend fun deleteAll()

    suspend fun findAll(): Flow<List<com.ivy.data.model.ExchangeRate>>

    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String,
    ): com.ivy.data.model.ExchangeRate?
}
