package com.ivy.parser

/**
 * Builds a new parser that do **Parser 1 || Parser 2**. Tries _Parser 1_ and
 * if it succeeds returns its result. If _Parser 1_ fails executes _Parser 2_.
 * Left associative operator for chaining parsers.
 *
 * **Example**
 * ```
 * // parser Calculator operation
 * enum Operation { Plus, Minus, Multiple, Divide }
 * fun operationParser(): Parser<Operation> =
 *      (char('+') or char('-') or char('*') or char('-')).apply { opSymbol ->
 *          when(opSymbol) {
 *              '+' -> Operation.Plus
 *              '-' -> Operation.Minus
 *              '*' -> Operation.Multiple
 *              '/' -> Operation.Divide
 *              else -> error("should NOT happen!")
 *          }
 *      }
 * ```
 *
 * @receiver the first parser to apply _(Parser 1)_.
 * @param parser2 the second parser to apply _(Parser 2)_.
 * @return a combined OR parser: _Parser 1_ **||** _Parser 2_.
 */
infix fun <T> Parser<T>.or(parser2: Parser<T>): Parser<T> = { text ->
    this(text).ifEmpty { parser2(text) }
}

/**
 * Applies _Parser 1_ then _Parser 2_ and returns their results combined.
 *
 * **Example:**
 * ```
 * fun parseAsciiA(): Parser<Int> = char('A').apply { char ->
 *  char.toByte().toInt()
 * }
 *
 * fun combined(): Parser<Any> = char('A') + parseAsciiA()
 * // ['A', 65]
 * ```
 *
 * @receiver _Parser 1_
 * @param parser2 _Parser 2_
 * @return  the combined result or Parser 1 + Parser 2: [[Parser 1]] + [[Parser 2]]
 */
operator fun <T> Parser<T>.plus(parser2: Parser<T>): Parser<T> = { text ->
    this(text) + parser2(text)
}

/**
 * Takes only the first variation of a parsing.
 * Parsers always return a list of results which may contain more than one parsings.
 * @return a parser that:
 * **[[ParserRes1, ParserRes2, ParserResN]] => [[ParserRes1]]**
 */
fun <T> Parser<T>.first(): Parser<T> = { text ->
    val res = this(text)
    res.take(1)
}

// region Occurrences
/**
 * Zero or many occurrences of a parser.
 */
fun <T> zeroOrMany(parser: Parser<T>): Parser<List<T>> {
    fun <T> oneOrMany(parser: Parser<T>): Parser<List<T>> =
        parser.apply { one -> // this recursion will stop when "one" stops returning
            zeroOrMany(parser).apply { zeroOrMany ->
                pure(listOf(one) + zeroOrMany)
            }
        }

    // If "oneOrMany" fails to parse, a.k.a returns failure []
    // then to hold the "zero" part true, return a successful parsing of an empty list of T
    val allVariations = oneOrMany(parser) + pure(emptyList())

    // this recursion returns an array of all occurrences of the parsed value
    // example: zeroMany(char('a')).invoke("aaa") will return:
    // [ParseResult(value=[a, a, a], leftover=), ParseResult(value=[a, a], leftover=),
    // ParseResult(value=[a], leftover=aa), ParseResult(value=[], leftover=aaa)]
    // => we need to take only the most result with the most occurrences
    // which happens to be at index 0 or first
    return allVariations.first()
}

/**
 * One or many occurrences of a parser.
 */
fun <T> oneOrMany(parser: Parser<T>): Parser<List<T>> = parser.apply { one ->
    // parsed one occurrence successfully
    zeroOrMany(parser).apply { zeroOrMany ->
        pure(listOf(one) + zeroOrMany)
    }
}

/**
 * Zero or one occurrences of a parser. This operation cannot fail.
 */
fun <T> optional(parser: Parser<T>): Parser<T?> = { text ->
    val result = parser(text)
    // if the parser fails it returns empty result
    // in case of failure to satisfy "zero" return a successful null result
    result.ifEmpty { listOf(ParseResult(null, text)) }
}
// endregion

/**
 * Returns a list of T values separated by something.
 * This operation will never fail. In case of failure will simple return a value empty list.
 */
fun <T, R> Parser<T>.separatedBy(separator: Parser<R>): Parser<List<T>> {
    fun Parser<T>.oneOrManySepBy(separator: Parser<R>): Parser<List<T>> = this.apply { one ->
        zeroOrMany(
            separator.apply {
                this
            }
        ).apply { manySeparated ->
            pure(listOf(one) + manySeparated)
        }
    }
    // the same pattern as in "zeroOrMany"
    val allVariations = this.oneOrManySepBy(separator) + pure(emptyList())
    return allVariations.first()
}