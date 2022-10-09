package com.ivy.math.calculator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AppendCalculatorOptionTest : FreeSpec({
    "appending + to expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" will become \"$expected\""
            },
            // Expression before (becomes) Expression (after)
            row("", "+"),
            row("3", "3+"),
            row("5+", "5+"), // doesn't apply double plus
            row("(", "(+"),
            row("2/", "2/+"),
            row("*", "*+"),
            row("%", "%+"),
            row("-", "-"),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOption.Plus)

            res shouldBe expected
        }
    }
})