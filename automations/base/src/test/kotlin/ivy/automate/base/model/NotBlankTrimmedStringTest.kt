package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.NotBlankTrimmedString

class NotBlankTrimmedStringTest : FreeSpec({
    "invalid" - {
        "blank string" {
            NotBlankTrimmedString.from("").shouldBeLeft()
            NotBlankTrimmedString.from(" ").shouldBeLeft()
            NotBlankTrimmedString.from("  ").shouldBeLeft()
        }
    }

    "valid" {
        // given
        val token = " abc "

        // when
        val res = NotBlankTrimmedString.from(token)

        // then
        res.shouldBeRight().value shouldBe "abc"
    }
})