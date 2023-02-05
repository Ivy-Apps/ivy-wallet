package com.ivy.core.persistence.api

import arrow.core.Either
import com.ivy.core.data.sync.SyncState
import com.ivy.core.persistence.api.data.PersistenceError
import com.ivy.core.persistence.api.data.Saveable

interface WriteSyncable<T, TID> {
    suspend fun save(
        item: Saveable<T>
    ): Either<PersistenceError, Unit>

    suspend fun saveMany(
        items: List<Saveable<T>>,
    ): Either<PersistenceError, Unit>

    suspend fun flag(
        id: TID,
        sync: SyncState
    ): Either<PersistenceError, Unit>

    suspend fun flagMany(
        ids: List<TID>,
        sync: SyncState
    ): Either<PersistenceError, Unit>

    suspend fun delete(id: TID): Either<PersistenceError, Unit>
    suspend fun deleteMany(ids: List<TID>): Either<PersistenceError, Unit>
}