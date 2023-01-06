package com.ivy.math.calculator

import com.ivy.math.evaluate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ExpressionUtilTest : FreeSpec({
    "classifies an expression" - {
        withData(
            nameFn = { (expression, obvious) ->
                "\"$expression\" as ${if (obvious) "obvious" else "NOT obvious"}"
            },
            // Expression (as) Obvious?
            row("32", true),
            row("513.00+", true),
            row("513.00+1", false),
            row("513.00+(", false),
            row("10*", true),
            row("10/", true),
            row("0.24-", true),
            row("8.00+9*", false),
        ) { (expression, obvious) ->
            val value = evaluate(expression)

            val res = hasObviousResult(expression, value)

            res shouldBe obvious
        }
    }

    "beautifies an expression" - {
        withData(
            nameFn = { (expression, beautified) ->
                "\"$expression\" beautified to \"$beautified\""
            },
            // Expression (as) Beautified expression
            row("", null),
            row("3.14", "3.14"),
            row("5+5", "5+5"),
            row("1024", "1,024"),
            row("1000000", "1,000,000"),
            row("10123.45678", "10,123.45678"),
        ) { (expression, beautified) ->
            val res = beautify(expression)

            res shouldBe beautified
        }
    }
})