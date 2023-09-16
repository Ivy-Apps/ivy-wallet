package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.LoanRecordEntity
import com.ivy.core.data.db.write.dao.WriteLoanRecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class LoanRecordWriter @Inject constructor(
    private val dao: WriteLoanRecordDao,
) {
    suspend fun save(value: LoanRecordEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<LoanRecordEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }

    suspend fun flagDeleted(id: UUID) {
        withContext(Dispatchers.IO) {
            dao.flagDeleted(id)
        }
    }
}