package com.ivy.parser

import kotlin.coroutines.suspendCoroutine

class ParserEffect {
    private var lastParser: Parser<Any?> = pure(Unit)

    /**
     * This isn't work! Experimenting!
     */
    suspend fun <R> Parser<R>.bindBroken(): R = suspendCoroutine { cont ->
        var rSave: R? = null
        lastParser = lastParser.apply { t ->
            this@bindBroken.apply { r ->
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

fun original() = string("Iliyan").apply { firstName ->
    item().apply {
        string("Germanov").apply { lastName ->
            pure(Person(firstName, lastName))
        }
    }
}

suspend fun personalParser() = parser {
    val firstName = string("Iliyan").bindBroken()
    item().bindBroken()
    val lastName = string("Nicole").bindBroken()
    pure(Person(firstName, lastName))
}