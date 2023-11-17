package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NotBlankTrimmedStringTest : FreeSpec({
    "fails for blank strings" {
        NotBlankTrimmedString.from("").shouldBeLeft()
        NotBlankTrimmedString.from(" ").shouldBeLeft()
        NotBlankTrimmedString.from("  ").shouldBeLeft()
    }

    "trims blanks space" {
        // given
        val rawInput = " abc "

        // when
        val res = NotBlankTrimmedString.from(rawInput)

        // then
        res.shouldBeRight() shouldBe NotBlankTrimmedString("abc")
    }
})