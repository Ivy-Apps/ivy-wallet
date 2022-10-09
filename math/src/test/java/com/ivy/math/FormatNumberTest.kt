package com.ivy.math

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class FormatNumberTest : FreeSpec({
    "formats number" - {
        withData(
            nameFn = { (number, expected) -> "$number as \"$expected\"" },
            // Number (as) String
            row(5.0, "5"),
            row(3.141_323, "3.141323"),
            row(0.005, "0.005"),
            row(1_024.0, "1,024"),
            row(10_030.25, "10,030.25"),
            row(1.000_004_000_00, "1.000004"),
            row(-1.0, "-1"),
            row(.5, "0.5"),
            row(0.25, "0.25"),
        ) { (number, expected) ->
            val res = formatNumber(number)

            res shouldBe expected
        }
    }
})