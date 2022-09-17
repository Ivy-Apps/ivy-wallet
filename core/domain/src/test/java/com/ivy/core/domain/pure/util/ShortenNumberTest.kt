package com.ivy.core.domain.pure.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ShortenNumberTest : StringSpec({
    "shorten 105,234 into 105.23k" {
        val res = shorten(105_234.0)

        res shouldBe "105.23k"
    }

    "shorten 23,508 into 23.51k" {
        val res = shorten(23_508.0)

        res shouldBe "23.51k"
    }

    "shorten 999.99 into 999.99" {
        val res = shorten(999.99)

        res shouldBe "999.99"
    }

    "shorten 10,000,000.90 into 10m" {
        val res = shorten(10_000_000.90)

        res shouldBe "10m"
    }
})