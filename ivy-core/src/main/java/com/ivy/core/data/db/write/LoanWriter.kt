package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.LoanEntity
import com.ivy.core.data.db.write.dao.WriteLoanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class LoanWriter @Inject constructor(
    private val dao: WriteLoanDao,
) {
    suspend fun save(value: LoanEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<LoanEntity>): Either<String, Unit> {
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