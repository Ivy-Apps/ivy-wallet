package com.ivy.data.model.primitive

import com.ivy.data.model.primitive.IconAsset
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue

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
        IconAsset.from(" iCoN ")
            .isRight { it.id == "icon" }
            .shouldBeTrue()
    }
})