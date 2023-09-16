package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.CategoryEntity
import com.ivy.core.data.db.write.dao.WriteCategoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryWriter @Inject constructor(
    private val dao: WriteCategoryDao,
) : DbWriter<CategoryEntity> {
    override suspend fun save(value: CategoryEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<CategoryEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}