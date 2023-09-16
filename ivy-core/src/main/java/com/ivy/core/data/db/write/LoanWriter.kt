package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.LoanEntity
import com.ivy.core.data.db.write.dao.WriteLoanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoanWriter @Inject constructor(
    private val dao: WriteLoanDao,
) : DbWriter<LoanEntity> {
    override suspend fun save(value: LoanEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<LoanEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}