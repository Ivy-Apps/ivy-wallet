package com.ivy.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class LexicalCombinatorsTest : FreeSpec({
    "parses whitespace" - {
        withData(
            nameFn = { (text, value, leftover) ->
                "in \"$text\" text as ${value.map { "'$it'" }} with \"$leftover\" leftover"
            },
            // Text (as) Whitespace values (with) Leftover
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

    "parses symbolic token" - {
        withData(
            nameFn = { (token, text, leftover) ->
                "\"$token\" in text \"$text\" with \"$leftover\" leftover"
            },
            // Token (in) Text (with) Leftover
            row("abc", "abc  text", "text"),
            row("okay", "okay\tnice", "nice"),
        ) { (token, text, leftover) ->
            val parser = symbolicToken(token)

            val res = parser(text)

            res shouldBe listOf(ParseResult(token, leftover))
        }
    }

    "fails to parses symbolic token" - {
        withData(
            nameFn = { (token, text) ->
                "\"$token\" in text \"$text\""
            },
            // Token (in) Text
            row("test", ""),
            row("okay", "ok\tnice"),
            row("cool", " cool"),
        ) { (token, text) ->
            val parser = symbolicToken(token)

            val res = parser(text)

            res shouldBe emptyList()
        }
    }
})