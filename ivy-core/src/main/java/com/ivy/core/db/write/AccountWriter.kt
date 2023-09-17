package com.ivy.core.db.write

import arrow.core.Either
import com.ivy.core.db.entity.AccountEntity
import com.ivy.core.db.write.dao.WriteAccountDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class AccountWriter @Inject constructor(
    val dao: WriteAccountDao,
) {
    suspend fun save(value: AccountEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<AccountEntity>): Either<String, Unit> {
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