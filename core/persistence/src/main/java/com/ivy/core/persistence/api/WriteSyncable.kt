package com.ivy.core.persistence.api

import arrow.core.Either
import com.ivy.core.persistence.api.data.PersistenceError
import com.ivy.core.persistence.api.data.WithSync

interface WriteSyncable<T, TID> {
    suspend fun save(
        item: WithSync<T>
    ): Either<PersistenceError, Unit>

    suspend fun saveMany(
        items: List<WithSync<T>>,
    ): Either<PersistenceError, Unit>

    suspend fun delete(id: TID): Either<PersistenceError, Unit>
    suspend fun deleteMany(ids: List<TID>): Either<PersistenceError, Unit>
}