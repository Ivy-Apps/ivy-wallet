package com.ivy.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ParserTest : FreeSpec({
    // region Parse Char
    "parses char" - {
        withData(
            // Char (in) Text (with) Leftover
            nameFn = { (char, text, leftover) ->
                "'$char' in text \"$text\" with \"$leftover\" leftover"
            },
            row('a', "a", ""),
            row('b', "back", "ack"),
            row('T', "T T", " T"),
        ) { (char, text, leftover) ->
            val parser = char(char)

            val res = parser(text)

            res shouldBe listOf(ParseResult(char, leftover))
        }
    }

    "fails to parse char" - {
        withData(
            nameFn = { (char, text) ->
                "'$char' in text \"$text\""
            },
            // Char (in) Text
            row('a', ""),
            row('b', "a"),
            row('c', "ac"),
        ) { (char, text) ->
            val parser = char(char)

            val res = parser(text)

            res shouldBe emptyList()
        }
    }
    // endregion

    // region Parse String
    "parses string" - {
        withData(
            nameFn = { (str, text, leftover) ->
                "\"$str\" in text \"$text\" with \"$leftover\" leftover "
            },
            // String (in) Text (with) Leftover
            row("aba", "aba", ""),
            row("okay", "okay Google", " Google"),
            row("zZ", "zZZz", "Zz"),
        ) { (str, text, leftover) ->
            val parser = string(str)

            val res = parser(text)

            res shouldBe listOf(ParseResult(str, leftover))
        }
    }

    "fails to parse string" - {
        withData(
            nameFn = { (str, text) ->
                "\"$str\" in text \"$text\""
            },
            // String (in) Text
            row("cat", "car"),
            row("Test", "test"),
            row("Itworks!", "It works!")
        ) { (str, text) ->
            val parser = string(str)

            val res = parser(text)

            res shouldBe emptyList()
        }
    }
    // endregion
})