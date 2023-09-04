package com.ivy.wallet

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.kotest.property.forAll

class PropertyTestExample : StringSpec({
    "length should return size of string" {
        "hello".length shouldBe 5
    }

    "startsWith should test for a prefix" {
        "world" should startWith("wor")
    }

    "String size" {
        forAll<String, String> { a, b ->
            a.length + b.length == (a + b).length
        }
    }
})
