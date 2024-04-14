package com.ivy.data.model.exact

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either

class ExactError(msg: String) : IllegalArgumentException(msg)

interface Exact<Value, out ExactValue> {
    val exactName: String

    fun Raise<String>.spec(raw: Value): ExactValue

    fun from(value: Value): Either<String, ExactValue> = either { spec(value) }
        .mapLeft { "$exactName error: $it" }

    /**
     * Turns a [Value] into an [ExactError] if it matches the spec.
     * Otherwise throws a runtime exception.
     * @throws ExactError if the [value] doesn't match the spec.
     */
    @Throws(ExactError::class)
    fun unsafe(value: Value): ExactValue = from(value).fold(
        ifLeft = { throw ExactError(it) },
        ifRight = { it },
    )
}
