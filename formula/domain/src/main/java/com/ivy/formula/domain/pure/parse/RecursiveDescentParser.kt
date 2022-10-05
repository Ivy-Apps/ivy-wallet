package com.ivy.formula.domain.pure.parse

/**
 * Motivated by FUNCTIONAL PEARL
 * Monadic parsing in Haskell
 * by Graham Hutton & Erik Meijer
 */

data class ParseResult<T>(
    val value: T,
    val leftover: String,
)

typealias Parser<T> = (String) -> List<ParseResult<T>>

fun <T> parse(
    string: String, parser: Parser<T>
): List<ParseResult<T>> = parser(string)

fun <T> pure(value: T): Parser<T> = { string ->
    listOf(ParseResult(value, string))
}

fun <T> empty(): Parser<T> = { emptyList() }

fun <T, R> bind(
    parser1: Parser<T>,
    f: (T) -> Parser<R>
): Parser<R> = { string ->
    val result = parser1(string) // apply parser 1

    // apply parser 2 on the result of parser 1
    result.flatMap {
        f(it.value).invoke(it.leftover)
    }
}

infix fun <T> Parser<T>.or(parser2: Parser<T>): Parser<T> = { string ->
    this(string).takeIf { it.isNotEmpty() } ?: parser2(string)
}

fun <T> combine(parser1: Parser<T>, parser2: Parser<T>): Parser<T> = { string ->
    parser1(string) + parser2(string)
}

// region Functions
fun item(): Parser<Char> = { string ->
    if (string.isNotEmpty()) {
        // return the first character as value and the rest as leftover
        listOf(
            ParseResult(
                value = string.first(),
                leftover = string.drop(1)
            )
        )
    } else emptyList()
}

fun peek(): Parser<Char> = { string ->
    if (string.isNotEmpty()) {
        listOf(ParseResult(value = string.first(), leftover = string))
    } else emptyList()
}

/**
 * Satisfies a given predicate.
 */
fun sat(predicate: (Char) -> Boolean): Parser<Char> = { string ->
    val resultParser = bind(item()) {
        if (predicate(it)) pure(it) else empty()
    }
    resultParser(string)
}
// endregion