package com.ivy.core.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.SettingsEntity
import com.ivy.core.data.db.write.dao.WriteSettingsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsWriter @Inject constructor(
    private val dao: WriteSettingsDao,
) {
    suspend fun save(value: SettingsEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<SettingsEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
    }
}