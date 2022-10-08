package com.ivy.parser

/**
 * Parses whitespace ' ' (space), '\t' tab or '\n'.
 * This operation never fails. For empty text, simple returns empty list of chars.
 */
fun whitespace(): Parser<List<Char>> = zeroOrMany(sat { it.isWhitespace() })

fun <T> token(parser: Parser<T>): Parser<T> = parser.apply { t ->
    whitespace().apply {
        pure(t)
    }
}

fun symbolicToken(str: String): Parser<String> = token(string(str))