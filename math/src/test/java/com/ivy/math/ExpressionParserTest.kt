package com.ivy.math

import com.ivy.parser.ParseResult
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ExpressionParserTest : FreeSpec({
    "evaluates an expression" - {
        withData(
            nameFn = { (expression, value) ->
                "\"$expression\" = $value"
            },
            // Expression (=) Value
            row("3.14", 3.14),
            row("2+2", 4.0),
            row("5-5", 0.0),
            row("6*9", 54.0),
            row("17/4", 4.25),
            row("2+3*2", 8.0),
            row("3*3.0*3", 27.0),
            row("(-7.5)+3.25-1*1", -5.25),
            row("(((24000-1400)*10%)-7200.50*6)/4", -10_235.75),
            row("-(5)", -5.0),
            row("-(3*3)", -9.0),
            row("(0.5)", 0.5),
            row("((20/5)*4)", 16.0),
            row("(-(-1))", 1.0),
            row("(2+2)%", 0.04),
            row("10%*200", 20.0),
            row("25%+0.75", 1.0),
            row("1-1+1-1", 0.0),
            row("1-1", 0.0),
            row("1-1-1", -1.0),
            row("1-1-1-1", -2.0),
            row("-8-4+2", -10.0),
            row("1000000-(12*12534-12*12534*10%)*80%", 891706.24),
        ) { (expression, expectedValue) ->
            val parser = expressionParser()

            val parseResults = parser(expression)
            val res = parseResults.map {
                val expressionTree = it.value
                val value = expressionTree.eval()
                println(
                    "\"$expression\" becomes \"${expressionTree.print()}\" and evaluates as $value"
                )
                ParseResult(value, it.leftover)
            }

            res shouldBe listOf(ParseResult(expectedValue, ""))
        }
    }
})