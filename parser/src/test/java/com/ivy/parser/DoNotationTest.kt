package com.ivy.parser

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DoNotationTest : StringSpec({
    "parse first and last name divider with anything" {
        runBlocking {
            val res = personalParser().invoke("Iliyan Germanov")

            res shouldBe Person(
                firstName = "Iliyan",
                lastName = "Germanov"
            )
        }
    }
})