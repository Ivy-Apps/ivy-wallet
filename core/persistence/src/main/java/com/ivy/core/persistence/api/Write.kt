package com.ivy.core.persistence.api

import arrow.core.Either
import com.ivy.core.persistence.api.data.PersistenceError

interface Write<T, TID> {
    suspend fun save(
        item: T
    ): Either<PersistenceError, Unit>

    suspend fun saveMany(
        items: List<T>,
    ): Either<PersistenceError, Unit>

    suspend fun delete(id: TID): Either<PersistenceError, Unit>
    suspend fun deleteMany(ids: List<TID>): Either<PersistenceError, Unit>
}