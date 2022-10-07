package com.ivy.parser

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParserTest : StringSpec({
    // region Chain parsers
    "parses 'abc' in \"abc\"" {
        val parser = char('a').apply { a ->
            char('b').apply { b ->
                char('c').apply { c ->
                    pure("$a$b$c")
                }
            }
        }

        val res = parser("abc")

        res shouldBe success(ParseResult(value = "abc", leftover = ""))
    }

    "fails for 'abc' in \"ab\"" {
        val parser = char('a').apply { a ->
            char('b').apply { b ->
                char('c').apply { c ->
                    pure("$a$b$c")
                }
            }
        }

        val res = parser("ab")

        res shouldBe failure()
    }
    // endregion

    // region zeroOrMany
    "parses zero a's" {
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
    // endregion

    // region oneOrMany

    // endregion
})