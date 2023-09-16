package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.TransactionEntity
import com.ivy.core.data.db.write.dao.WriteTransactionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionWriter @Inject constructor(
    private val dao: WriteTransactionDao,
) : DbWriter<TransactionEntity> {
    override suspend fun save(value: TransactionEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<TransactionEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}