package com.ivy.parser

/**
 * Motivated by FUNCTIONAL PEARL
 * Monadic parsing in Haskell
 * by Graham Hutton & Erik Meijer
 */
// Paper: https://www.cs.nott.ac.uk/~pszgmh/pearl.pdf

/**
 * @param value the parsed value
 * @param leftover the text left to parse
 */
data class ParseResult<out T>(
    val value: T,
    val leftover: String,
)

/**
 * Parser monad which accepts text (String)
 * and returns a list of parse interpretations or [] on failure.
 */
typealias Parser<T> = (String) -> List<ParseResult<T>>

// region Result builders
/**
 * Use for successfully parsing a value.
 * Wraps a value in a parse w/o modifying the text being parsed.
 *
 * **Haskell equivalent:**
 * - Applicative#pure()
 * - Monad#return()
 */
fun <T> pure(value: T): Parser<T> = { text ->
    listOf(ParseResult(value, text))
}

/**
 * Returns a parser indicating failure which will fail all parsers applied after it.
 */
fun <T> fail(): Parser<T> = { emptyList() }
// endregion

/**
 * Applies a parser and invokes the parser with parsed value if it was successful.
 * In case of multiple successful parsing returned
 * from this parse or next parser, they're flattened.
 *
 * **FP equivalent:**
 * - Monad#bind
 * - Scala's flatMap{}
 *
 * **Example:**
 * ```
 * // parse the text "Jetpack Compose" or "Jetpack+Compose"
 * fun jetpackComposeParser() = string("Jetpack").apply { jetpack ->
 *  (char(' ') or char('+')).apply { //ignored divider
 *      string("Compose").apply { compose ->
 *          pure(jetpack + compose)
 *      }
 *  }
 * }
 * ```
 *
 * @receiver the first parser to apply _(Parser 1)_.
 * @param nextParser a function creating the next parser which will be applied only if
 * _Parser 1_ was successful.
 * @return a new parser that chains _"Parser 1 -> Parser 2"_.
 *
 */
fun <T : Any?, R : Any?> Parser<T>.apply(
    nextParser: (T) -> Parser<R>
): Parser<R> = { string ->
    val res1 = this(string) // apply parser 1

    /*
     * Parser 1 = "this"
     * Parser 2 = "nextParser"
     * # Case A:
     * Parser 1 fails, meaning it returns res1 = []
     * => Parser 2 won't be invoked, [] (failure) is returned
     *
     * # Case B:
     * Parser 1 parses only one thing: res1 = [ParseResult<T>]
     * => Parser 2 will be invoked only once, [f(ParseResult<T>)]
     *
     * # Case C:
     * Parser 1 parses multiple things: res1 = [pr1, pr2, ... , prn]
     * => Parser 2 (`f`) will be invoked N times.
     * If Parser 2 also returns multiple results they'll be flattened and returned [n*m]
     * where n = Parser 1 results and m = Parser 2 results for each n.
     */
    res1.flatMap {
        // Apply Parser 2 to each successfully parsed value by Parser 1 and its leftover
        nextParser(it.value).invoke(it.leftover)
    }
}

// region Read a not parsed character
/**
 * A parser that reads one character from the text left to parse.
 * Fails if the text is empty.
 */
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
// endregion

// region Core: Parse char, string & a symbol satisfying a predicate
/**
 * Parses a char if it satisfies a given predicate.
 * @param predicate returns whether the parsing is successful.
 * @return a parser that parses a character for a predicate.
 */
fun sat(predicate: (Char) -> Boolean): Parser<Char> = { string ->
    item().apply { char ->
        if (predicate(char)) pure(char) else fail()
    }.invoke(string)
}

/**
 * Parses a specific character.
 * @param c the character to parse
 * @return a parser that parses a character
 */
fun char(c: Char): Parser<Char> = sat { it == c }

fun string(str: String): Parser<String> = { string ->
    if (str.isEmpty()) pure("").invoke(string) else {
        // recurse
        char(str.first()).apply { c ->
            string(str.drop(1)).apply { cs ->
                pure(c + cs)
            }
        }.invoke(string)
    }
}
// endregion