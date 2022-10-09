package com.ivy.parser

import com.ivy.parser.common.letter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

class AlphabetParserTest : FreeSpec({
    "parses a letter" {
        val allLetters = ('a'..'z') + ('A'..'Z')
        checkAll(allLetters.exhaustive()) { letter ->
            val parser = letter()

            val res = parser(letter.toString())

            res shouldBe listOf(ParseResult(letter, ""))
        }
    }

    "fails to parse a non-letter" {
        checkAll(Arb.char(listOf('!'..'0', '{'..'~'))) { nonLetter ->
            val parser = letter()

            val res = parser(nonLetter.toString())

            res shouldBe emptyList()
        }
    }
})