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
})