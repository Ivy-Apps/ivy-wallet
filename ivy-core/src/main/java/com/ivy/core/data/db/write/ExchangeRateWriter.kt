package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.ExchangeRateEntity
import com.ivy.core.data.db.write.dao.WriteExchangeRatesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRateWriter @Inject constructor(
    private val dao: WriteExchangeRatesDao,
) : DbWriter<ExchangeRateEntity> {
    override suspend fun save(value: ExchangeRateEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<ExchangeRateEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}