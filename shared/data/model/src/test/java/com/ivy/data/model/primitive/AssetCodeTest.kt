package com.ivy.data.model.primitive

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import org.junit.Test

class AssetCodeTest {
    @Test
    fun `fails for blank asset codes`() {
        AssetCode.from("").shouldBeLeft()
        AssetCode.from(" ").shouldBeLeft()
        AssetCode.from("   ").shouldBeLeft()
    }

    @Test
    fun `asset codes should be always uppercase and trimmed`() {
        // given
        val rawInput = " usd "

        // when
        val res = AssetCode.from(rawInput)

        // then
        res.shouldBeRight() shouldBe AssetCode.unsafe("USD")
    }
}
