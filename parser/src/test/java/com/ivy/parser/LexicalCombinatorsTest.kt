package com.ivy.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class LexicalCombinatorsTest : FreeSpec({
    "parses spaces" - {
        withData(
            nameFn = { (text, value, leftover) ->
                "in \"$text\" text as ${value.map { "'$it'" }} with \"$leftover\" leftover"
            },
            row(" Iliyan", listOf(' '), "Iliyan"),
            row("   a b c", listOf(' ', ' ', ' '), "a b c"),
            row("", listOf(), ""),
            row("\n\n1", listOf('\n', '\n'), "1"),
            row("\tOkay\t", listOf('\t'), "Okay\t"),
        ) { (text, value, leftover) ->
            val parser = whitespace()

            val res = parser(text)

            res shouldBe listOf(ParseResult(value, leftover))
        }
    }
})