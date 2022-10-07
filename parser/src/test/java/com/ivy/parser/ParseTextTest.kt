package com.ivy.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParseTextTest : StringSpec({
    data class Given<T>(val text: String, val value: T)

    // region Parse Char
    listOf(
        Given("a", 'a') to ParseResult('a', ""),
        Given("back", 'b') to ParseResult('b', "ack"),
        Given("T", 'T') to ParseResult('T', ""),
    ).forEach { (given, expected) ->
        "parses '${given.value}' in \"${given.text}\" with \"${expected.leftover}\" leftover" {
            val parser = char(given.value)

            val res = parser(given.text)

            res shouldBe successful(expected)
        }
    }

    // Fails for:
    listOf(
        Given(text = "", value = 'a'),
        Given(text = "a", value = 'b'),
        Given(text = "ac", value = 'c')
    ).forEach { (text, char) ->
        "fails to parse '$char' in \"$text\"" {
            val parser = char(char)

            val res = parser(text)

            res shouldBe failure()
        }
    }
    // endregion

    // region Parse String
    listOf(
        Given("aba", "aba") to ParseResult("aba", ""),
        Given("okay Google", "okay") to ParseResult("okay", " Google"),
        Given("zZZz", "zZ") to ParseResult("zZ", "Zz"),
    ).forEach { (given, expected) ->
        "parses \"${given.value}\" in \"${given.text}\" with \"${expected.leftover}\" leftover" {
            val parser = string(given.value)

            val res = parser(given.text)

            res shouldBe successful(expected)
        }
    }

    // Fails for:
    listOf(
        Given(text = "car", value = "cat"),
        Given(text = "test", value = "Test"),
    ).forEach { (text, str) ->
        "fails to parse \"$str\" to \"$text\"" {
            val parser = string(str)

            val res = parser(text)

            res shouldBe failure()
        }
    }
    // endregion
})