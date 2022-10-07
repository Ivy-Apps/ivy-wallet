package com.ivy.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParserTest : StringSpec({
    "parses zeros a's" {
        val parser = zeroOrMany(char('a'))

        val res = parser("")

        res shouldBe success(ParseResult(value = emptyList(), leftover = ""))
    }

    "parses multiple a's" {
        val parser = zeroOrMany(char('a'))

        val res = parser("aaa")

        res shouldBe success(ParseResult(value = listOf('a', 'a', 'a'), leftover = ""))
    }

    "parses multiple a's with a leftover" {
        val parser = zeroOrMany(char('a'))

        val res = parser("aaabbb")

        res shouldBe success(ParseResult(value = listOf('a', 'a', 'a'), leftover = "bbb"))
    }
})