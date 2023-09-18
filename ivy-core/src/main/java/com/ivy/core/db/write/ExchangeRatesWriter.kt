package com.ivy.core.db.write

import arrow.core.Either
import com.ivy.core.db.entity.ExchangeRateEntity
import com.ivy.core.db.write.dao.WriteExchangeRatesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRatesWriter @Inject constructor(
    private val dao: WriteExchangeRatesDao,
) {
    suspend fun save(value: ExchangeRateEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<ExchangeRateEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }

    suspend fun deleteByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String,
    ) {
        withContext(Dispatchers.IO) {
            dao.deleteByBaseCurrencyAndCurrency(baseCurrency, currency)
        }
    }
}