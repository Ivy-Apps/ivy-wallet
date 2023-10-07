package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class IconIdTest : FreeSpec({
    "fails for blank ids" {
        IconAsset.from("").shouldBeLeft()
        IconAsset.from(" ").shouldBeLeft()
        IconAsset.from("   ").shouldBeLeft()
    }

    "fails for icon ids containing spaces" {
        IconAsset.from("icon 1").shouldBeLeft()
    }

    "icon ids should be always lowercase and trimmed" {
        // given
        val rawInput = " iCoN "

        // when
        val res = IconAsset.from(rawInput)

        // then
        res.shouldBeRight() shouldBe IconAsset("icon")
    }
})