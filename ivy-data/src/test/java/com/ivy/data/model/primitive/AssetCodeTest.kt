package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AssetCodeTest : FreeSpec({
    "fails for blank asset codes" {
        AssetCode.from("").shouldBeLeft()
        AssetCode.from(" ").shouldBeLeft()
        AssetCode.from("   ").shouldBeLeft()
    }

    "asset codes should be always uppercase and trimmed" {
        // given
        val rawInput = " usd "

        // when
        val res = AssetCode.from(rawInput)

        // then
        res.shouldBeRight() shouldBe AssetCode("USD")
    }
})