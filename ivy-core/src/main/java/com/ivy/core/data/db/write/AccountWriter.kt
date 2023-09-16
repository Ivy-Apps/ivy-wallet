package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.AccountEntity
import com.ivy.core.data.db.write.dao.WriteAccountDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountWriter @Inject constructor(
    private val dao: WriteAccountDao,
) : DbWriter<AccountEntity> {
    override suspend fun save(value: AccountEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    override suspend fun saveMany(values: List<AccountEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }
}