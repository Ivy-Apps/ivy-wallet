package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.model.ExchangeRate
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.responses.ExchangeRatesResponse
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.mapper.ExchangeRateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val mapper: ExchangeRateMapper,
    private val exchangeRatesDao: ExchangeRatesDao,
    private val writeExchangeRatesDao: WriteExchangeRatesDao,
    private val remoteExchangeRatesDataSource: RemoteExchangeRatesDataSource,
    private val dispatchersProvider: DispatchersProvider,
) : ExchangeRatesRepository {

    override val urls = listOf(
        "https://currency-api.pages.dev/v1/currencies/eur.json",
        "https://currency-api.pages.dev/v1/currencies/eur.min.json",
        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.min.json",
        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json",
    )
    override suspend fun fetchExchangeRates() : ExchangeRatesResponse?{

        var result: ExchangeRatesResponse? = null

        withContext(dispatchersProvider.io){
            urls.forEach {url ->
                result = remoteExchangeRatesDataSource.fetchEurExchangeRates(url).getOrNull()
                if(result != null) return@withContext
            }
        }
        return result
    }

    override suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRate? {
        return withContext(dispatchersProvider.io){
            val exchangeRateEntity = exchangeRatesDao.findByBaseCurrencyAndCurrency(baseCurrency, currency)
            if(exchangeRateEntity != null){
                with(mapper){
                    return@withContext exchangeRateEntity.toDomain()
                }
            } else {
                return@withContext null
            }
        }
    }

    override suspend fun save(value: ExchangeRateEntity) {
        withContext(dispatchersProvider.io){
            writeExchangeRatesDao.save(value)
        }
    }

    override suspend fun save(value: ExchangeRate) {
        withContext(dispatchersProvider.io){
            writeExchangeRatesDao.save( with(mapper){ value.toEntity() } )
        }
    }

    override suspend fun saveManyEntities(values: List<ExchangeRateEntity>) {
        withContext(dispatchersProvider.io){
            writeExchangeRatesDao.saveMany(values)
        }
    }

    override suspend fun saveManyRates(values: List<ExchangeRate>) {
        withContext(dispatchersProvider.io){
            writeExchangeRatesDao.saveMany(
                values.map {
                    with(mapper) { it.toEntity() }
                }
            )
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io){
            writeExchangeRatesDao.deleteALl()
        }
    }

    override suspend fun findAll(): Flow<List<ExchangeRate>> {
        return withContext(dispatchersProvider.io){
            exchangeRatesDao.findAll().map {exchangeRateEntities ->
                with(mapper){
                    exchangeRateEntities.map { it.toDomain() }
                }
            }
        }
    }
}