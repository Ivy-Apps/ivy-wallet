package com.ivy.parser

import com.ivy.parser.common.int
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class NumberParserTest : FreeSpec({
    "parses an integer" - {
        withData(
            nameFn = { (text, number, leftover) ->
                "from text \"$text\" as $number with \"$leftover\" leftover"
            },
            // Text (as) Number (with) Leftover
            row("0", 0, ""),
            row("+1", 1, ""),
            row("1", 1, ""),
            row("-1", -1, ""),
            row("123456", 123456, ""),
            row("-123456", -123456, ""),
            row("900ok", 900, "ok"),
            row("-5+10", -5, "+10"),
            row("01070 ", 1070, " "),
        ) { (text, number, leftover) ->
            val parser = int()

            val res = parser(text)

            res shouldBe listOf(ParseResult(number, leftover))
        }
    }
})