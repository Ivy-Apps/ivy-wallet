package com.ivy.domain.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue

class NotBlankTrimmedStringTest : FreeSpec({
    "fails for blank strings" {
        NotBlankTrimmedString.from("").shouldBeLeft()
        NotBlankTrimmedString.from(" ").shouldBeLeft()
        NotBlankTrimmedString.from("  ").shouldBeLeft()
    }

    "trims blanks space" {
        NotBlankTrimmedString.from(" abc ")
            .isRight { it.value == "abc" }
            .shouldBeTrue()
    }
})