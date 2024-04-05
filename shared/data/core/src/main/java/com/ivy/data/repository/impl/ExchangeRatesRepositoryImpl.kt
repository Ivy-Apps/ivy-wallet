package com.ivy.data.repository.impl

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.mapper.ExchangeRateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val mapper: ExchangeRateMapper,
    private val exchangeRatesDao: ExchangeRatesDao,
    private val writeExchangeRatesDao: WriteExchangeRatesDao,
    private val remoteExchangeRatesDataSource: RemoteExchangeRatesDataSource,
    private val dispatchers: DispatchersProvider,
) : ExchangeRatesRepository {
    override suspend fun fetchEurExchangeRates(): Either<String, List<ExchangeRate>> = either {
        withContext(dispatchers.io) {
            val response = remoteExchangeRatesDataSource.fetchEurExchangeRates().bind()
            with(mapper) { response.toDomain().bind() }
        }
    }

    override fun findAll(): Flow<List<ExchangeRate>> =
        exchangeRatesDao.findAll().map { entities ->
            entities.mapNotNull {
                with(mapper) { it.toDomain().getOrNull() }
            }
        }.flowOn(dispatchers.io)

    override suspend fun findAllManuallyOverridden(): List<ExchangeRate> =
        withContext(dispatchers.io) {
            exchangeRatesDao.findAllManuallyOverridden()
                .mapNotNull {
                    with(mapper) { it.toDomain().getOrNull() }
                }
        }

    override suspend fun save(value: ExchangeRate) {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.save(with(mapper) { value.toEntity() })
        }
    }

    override suspend fun saveManyRates(values: List<ExchangeRate>) {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.saveMany(
                values.map {
                    with(mapper) { it.toEntity() }
                },
            )
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchers.io) {
            writeExchangeRatesDao.deleteALl()
        }
    }

    override suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: AssetCode,
        currency: AssetCode
    ): Unit = withContext(dispatchers.io) {
        writeExchangeRatesDao.deleteByBaseCurrencyAndCurrency(
            baseCurrency = baseCurrency.code,
            currency = currency.code
        )
    }
}
