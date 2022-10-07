package com.ivy.parser

import kotlin.coroutines.suspendCoroutine

class ParserEffect {
    private var lastParser: Parser<Any?> = pure(Unit)

    /**
     * This isn't work! Experimenting!
     */
    suspend fun <R> Parser<R>.bind(): R = suspendCoroutine { cont ->
        var rSave: R? = null
        lastParser = lastParser.flatMap { t ->
            this@bind.flatMap { r ->
                rSave = r
                pure(t)
            }
        }
        do {
            if (rSave != null) {
                cont.resumeWith(Result.success(rSave!!))
                break
            }
        } while (true)
    }
}

suspend fun <T> parser(parse: suspend ParserEffect.() -> Parser<T>): Parser<T> =
    with(ParserEffect()) {
        parse()
    }

data class Person(
    val firstName: String,
    val lastName: String,
)

suspend fun personalParser() = parser {
    val firstName = string("Iliyan").bind()
    item().bind()
    val lastName = string("Nicole").bind()
    pure(Person(firstName, lastName))
}