package com.ivy.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ParserCombinatorsTest : FreeSpec({
    "parses zero or one characters" - {
        withData(
            nameFn = { (char, text, value, leftover) ->
                "'$char' in text \"$text\" as value '$value' with '$leftover' leftover"
            },
            // Char (in) Text (as) Value (with) Leftover
            row('a', "aaa", 'a', "aa"),
            row('b', "", null, ""),
            row('c', "cool", 'c', "ool"),
            row('=', "5+5", null, "5+5"),
        ) { (c, text, value, leftover) ->
            val parser = zeroOrOne(char(c))

            val res = parser(text)

            res shouldBe listOf(ParseResult(value, leftover))
        }
    }
})