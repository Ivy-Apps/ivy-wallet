package com.ivy.math.calculator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next

class AppendNumberDigit : FreeSpec({
    "appending digit to an expression" - {
        val digit = Arb.int(0..9).next()
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" becomes \"$expected\""
            },
            // Expression (becomes) Expression after
            row("", "$digit"),
            row("1", "1$digit"),
            row("10%", "10%*$digit"),
            row("(5+5)", "(5+5)*$digit"),
        ) { (expression, expected) ->
            val res = appendTo(expression, digit)

            res shouldBe expected
        }
    }
})