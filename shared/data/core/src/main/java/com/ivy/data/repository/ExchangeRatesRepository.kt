package com.ivy.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.repository.mapper.ExchangeRateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val mapper: ExchangeRateMapper,
    private val exchangeRatesDao: ExchangeRatesDao,
    private val writeExchangeRatesDao: WriteExchangeRatesDao,
    private val remoteExchangeRatesDataSource: RemoteExchangeRatesDataSource,
    private val dispatchers: DispatchersProvider,
) {
    suspend fun fetchEurExchangeRates(): Either<String, List<ExchangeRate>> = either {
        withContext(dispatchers.io) {
            val response = remoteExchangeRatesDataSource.fetchEurExchangeRates().bind()
            with(mapper) { response.toDomain().bind() }
        }
    }

    fun findAll(): Flow<List<ExchangeRate>> =
        exchangeRatesDao.findAll().map { entities ->
            entities.mapNotNull {
                with(mapper) { it.toDomain().getOrNull() }
            }
        }.flowOn(dispatchers.io)

    suspend fun findAllManuallyOverridden(): List<ExchangeRate> =
        withContext(dispatchers.io) {
            exchangeRatesDao.findAllManuallyOverridden()
                .mapNotNull {
                    with(mapper) { it.toDomain().getOrNull() }
                }
        }

    suspend fun save(value: ExchangeRate) {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.save(with(mapper) { value.toEntity() })
        }
    }

    suspend fun saveManyRates(values: List<ExchangeRate>) {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.saveMany(
                values.map {
                    with(mapper) { it.toEntity() }
                },
            )
        }
    }

    suspend fun deleteAll() {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.deleteAll()
        }
    }

    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: AssetCode,
        currency: AssetCode
    ): Unit = withContext(dispatchers.io) {
        writeExchangeRatesDao.deleteByBaseCurrencyAndCurrency(
            baseCurrency = baseCurrency.code,
            currency = currency.code
        )
    }
}
