package com.ivy.core.functions.transaction

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SortTrnsTest : StringSpec({
    "case true" {
        true shouldBe true
    }

    "case false" {
        false shouldBe true
    }
})