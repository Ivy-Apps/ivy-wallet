package com.ivy.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class RecursionCombinatorsTest : FreeSpec({
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
            val parser = optional(char(c))

            val res = parser(text)

            res shouldBe listOf(ParseResult(value, leftover))
        }
    }

    "parses items separated by" - {
        withData(
            nameFn = { (separator, text, values, leftover) ->
                "'$separator' sep in text \"$text\" as values $values with \"$leftover\" leftover"
            },
            // Separator (in) Text (as) [Values] (with) Leftover
            row(",", "a,b,c,d", listOf('a', 'b', 'c', 'd'), ""),
            row("--", "a--b--c:test", listOf('a', 'b', 'c'), ":test"),
            row("--", "", listOf(), ""),
            row(" ", "okay", listOf('o'), "kay"),
        ) { (separator, text, values, leftover) ->
            val parser = item().separatedBy(string(separator))

            val res = parser(text)

            res shouldBe listOf(ParseResult(values, leftover))
        }
    }
})