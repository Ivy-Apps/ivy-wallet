package com.ivy.core.persistence.api

import arrow.core.Either
import arrow.core.NonEmptyList
import com.ivy.core.persistence.api.data.PersistenceError

interface Write<T, TID> {
    suspend fun save(
        item: T
    ): Either<PersistenceError, Unit>

    suspend fun saveMany(
        items: NonEmptyList<T>,
    ): Either<PersistenceError, Unit>

    suspend fun delete(id: TID): Either<PersistenceError, Unit>
    suspend fun deleteMany(ids: NonEmptyList<TID>): Either<PersistenceError, Unit>
}