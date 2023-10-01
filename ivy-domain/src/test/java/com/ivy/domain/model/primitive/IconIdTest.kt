package com.ivy.domain.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue

class IconIdTest : FreeSpec({
    "fails for blank ids" {
        IconId.from("").shouldBeLeft()
        IconId.from(" ").shouldBeLeft()
        IconId.from("   ").shouldBeLeft()
    }

    "fails for icon ids containing spaces" {
        IconId.from("icon 1").shouldBeLeft()
    }

    "icon ids should be always lowercase and trimmed" {
        IconId.from(" iCoN ")
            .isRight { it.id == "icon" }
            .shouldBeTrue()
    }
})