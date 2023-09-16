package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.BudgetEntity
import com.ivy.core.data.db.write.dao.WriteBudgetDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BudgetWriter @Inject constructor(
    private val dao: WriteBudgetDao,
) : DbWriter<BudgetEntity> {
    override suspend fun save(value: BudgetEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<BudgetEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}