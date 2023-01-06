package com.ivy.parser

/**
 * Parses whitespace ' ' (space), '\t' tab or '\n'.
 * This operation never fails. For empty text, simple returns empty list of chars.
 */
fun whitespace(): Parser<List<Char>> = zeroOrMany(sat { it.isWhitespace() })

/**
 * Parses a thing and then removes the whitespace after it.
 *
 * **Example**
 * ```
 * val parser = token(string("okay"))
 * parser("okay Google")
 * // ParserResult(value="okay", leftover = "Google")
 * ```
 */
fun <T> token(parser: Parser<T>): Parser<T> = parser.apply { t ->
    whitespace().apply {
        pure(t)
    }
}

/**
 * Parses a string and then remove the whitespace after it.
 * See [token].
 */
fun symbolicToken(str: String): Parser<String> = token(string(str))