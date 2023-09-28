package com.ivy.domain.exact

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either

class ExactError(msg: String) : IllegalArgumentException(msg)

interface Exact<Value, ExactValue> {
    fun Raise<String>.spec(raw: Value): ExactValue

    fun from(value: Value): Either<String, ExactValue> = either { spec(value) }

    operator fun invoke(value: Value): ExactValue = from(value).fold(
        ifLeft = { throw ExactError(it) },
        ifRight = { it },
    )
}