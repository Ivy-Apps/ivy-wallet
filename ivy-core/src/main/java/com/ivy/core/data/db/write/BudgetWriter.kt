package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.BudgetEntity
import com.ivy.core.data.db.write.dao.WriteBudgetDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class BudgetWriter @Inject constructor(
    private val dao: WriteBudgetDao,
) {
    suspend fun save(value: BudgetEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<BudgetEntity>): Either<String, Unit> {
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