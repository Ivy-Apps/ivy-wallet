package com.ivy.core.data.db.write

import arrow.core.Either

interface DbWriter<D> {
    suspend fun save(value: D): Either<String, Unit>
    suspend fun saveMany(values: List<D>): Either<String, Unit>
}