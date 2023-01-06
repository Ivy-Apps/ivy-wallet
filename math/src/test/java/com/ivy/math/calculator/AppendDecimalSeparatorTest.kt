package com.ivy.math.calculator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AppendDecimalSeparatorTest : FreeSpec({
    "appending '.' decimal separator" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" becomes \"$expected\""
            },
            // Expression (becomes) Expression after
            row("", "0."),
            row(".", "."),
            row("(10+5)", "(10+5)"),
            row("7", "7."),
            row("2%", "2%"),
        ) { (expression, expected) ->
            val res = appendDecimalSeparator(
                expression = expression, decimalSeparator = '.'
            )

            res shouldBe expected
        }
    }
})