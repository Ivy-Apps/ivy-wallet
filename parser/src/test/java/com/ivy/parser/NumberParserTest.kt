package com.ivy.parser

import com.ivy.parser.common.int
import com.ivy.parser.common.number
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class NumberParserTest : FreeSpec({
    // region Parse integer
    "parses an integer" - {
        withData(
            nameFn = { (text, number, leftover) ->
                "from text \"$text\" as $number with \"$leftover\" leftover"
            },
            // Text (as) Number (with) Leftover
            row("0", 0, ""),
            row("1", 1, ""),
            row("123456", 123456, ""),
            row("900ok", 900, "ok"),
            row("5+10", 5, "+10"),
            row("01070 ", 1070, " "),
        ) { (text, number, leftover) ->
            val parser = int()

            val res = parser(text)

            res shouldBe listOf(ParseResult(number, leftover))
        }
    }

    "fails to parse an integer" - {
        withData(
            nameFn = { (text) -> "from \"$text\" text" },
            row(" 3"),
            row("*10"),
            row("=8"),
        ) { (text) ->
            val parser = int()

            val res = parser(text)

            res shouldBe emptyList()
        }
    }
    // endregion

    // region Parse double
    "parses a decimal" - {
        withData(
            nameFn = { (text, double, leftover) ->
                "from \"$text\" text as $double with \"$leftover\" leftover"
            },
            // (from) Text (as) Double (with) Leftover
            row("0", 0.0, ""),
            row("3.14", 3.14, ""),
            row(".003", 0.003, ""),
            row(".5", 0.5, ""),
            row("1024wtf?", 1_024.0, "wtf?"),
            row("0.99", 0.99, ""),
            row("5.65+18", 5.65, "+18"),
            row("3.%*10", 3.0, "%*10"),
            row("7", 7.0, ""),
            row("5748b", 5748.0, "b"),
            row("1", 1.0, ""),
        ) { (text, double, leftover) ->
            val parser = number()

            val res = parser(text)

            res shouldBe listOf(ParseResult(double, leftover))
        }
    }

    "fails to parse a decimal" - {
        withData(
            nameFn = { (text) -> "from \"$text\" text" },
            // Text
            row(" 3.14"),
            row("a10"),
            row("..3"),
            row(""),
            row("."),
        ) { (text) ->
            val parser = number()

            val res = parser(text)

            res shouldBe emptyList()
        }
    }
    // endregion
})