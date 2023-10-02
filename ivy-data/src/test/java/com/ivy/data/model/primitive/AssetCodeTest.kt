package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue

class AssetCodeTest : FreeSpec({
    "fails for blank asset codes" {
        AssetCode.from("").shouldBeLeft()
        AssetCode.from(" ").shouldBeLeft()
        AssetCode.from("   ").shouldBeLeft()
    }

    "asset codes should be always uppercase and trimmed" {
        AssetCode.from(" usd ")
            .isRight { it.code == "USD" }
            .shouldBeTrue()
    }
})