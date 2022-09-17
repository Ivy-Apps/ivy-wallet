package com.ivy.core.domain.pure.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SplitDoubleTest : StringSpec({
    "split 4.24 into 4 and 0.24" {
        val res = split(4.24)

        res.intPart shouldBe 4
        res.decimalPart shouldBe 0.24
    }

    "split 154,024.0 into 154,024 and 0.0" {
        val res = split(154_024.0)

        res.intPart shouldBe 154_024
        res.decimalPart shouldBe 0.0
    }

    "split 0.0056 into 0 and 0.0056" {
        val res = split(0.0056)

        res.intPart shouldBe 0
        res.decimalPart shouldBe 0.0056
    }
})