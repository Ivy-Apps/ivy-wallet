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

fun <T> pure(value: T): Parser<T> = { text ->
    listOf(ParseResult(value, text))
}

fun <T> empty(): Parser<T> = { emptyList() }

fun <T, R> Parser<T>.flatMap(
    f: (T) -> Parser<R>
): Parser<R> = { string ->
    val result = this(string) // apply parser 1

    // apply parser 2 on the result of parser 1
    result.flatMap {
        f(it.value).invoke(it.leftover)
    }
}

infix fun <T> Parser<T>.or(parser2: Parser<T>): Parser<T> = { text ->
    this(text).takeIf { it.isNotEmpty() } ?: parser2(text)
}

fun <T> combine(parser1: Parser<T>, parser2: Parser<T>): Parser<T> = { text ->
    parser1(text) + parser2(text)
}

fun <T> combineFirst(parser1: Parser<T>, parser2: Parser<T>): Parser<T> = { text ->
    val res = combine(parser1, parser2).invoke(text)
    res.take(1)
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
    item().flatMap { char ->
        if (predicate(char)) pure(char) else empty()
    }.invoke(string)
}

fun char(c: Char): Parser<Char> = sat { it == c }

fun charIn(str: String): Parser<Char> = sat { str.contains(it) }

fun string(str: String): Parser<String> = { string ->
    if (str.isEmpty()) pure("").invoke(string) else {
        // recurse
        char(str.first()).flatMap { c ->
            string(str.drop(1)).flatMap { cs ->
                pure(c + cs)
            }
        }.invoke(string)
    }
}

fun <T> zeroOrMany(parser: Parser<T>): Parser<List<T>> {
    fun <T> oneOrMany(parser: Parser<T>): Parser<List<T>> =
        parser.flatMap { one ->
            zeroOrMany(parser).flatMap { many ->
                pure(listOf(one) + many)
            }
        }

    return combineFirst(oneOrMany(parser), pure(emptyList()))
}

fun <T> oneOrMany(parser: Parser<T>): Parser<List<T>> = parser.flatMap { one ->
    zeroOrMany(parser).flatMap { many ->
        pure(listOf(one) + many)
    }
}
// endregion