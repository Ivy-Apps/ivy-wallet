package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import org.junit.Test

class NotBlankTrimmedStringTest {
    @Test
    fun `fails for blank strings`() {
        NotBlankTrimmedString.from("").shouldBeLeft()
        NotBlankTrimmedString.from(" ").shouldBeLeft()
        NotBlankTrimmedString.from("  ").shouldBeLeft()
    }

    @Test
    fun `trims blanks space`() {
        // given
        val rawInput = " abc "

        // when
        val res = NotBlankTrimmedString.from(rawInput)

        // then
        res.shouldBeRight() shouldBe NotBlankTrimmedString.unsafe("abc")
    }
}
