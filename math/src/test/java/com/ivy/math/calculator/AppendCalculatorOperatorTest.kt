package com.ivy.math.calculator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AppendCalculatorOperatorTest : FreeSpec({
    "appending '+' to an expression" - {
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
            row("0.23-", "0.23-"),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOperator.Plus)

            res shouldBe expected
        }
    }

    "appending '-' to an expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" will become \"$expected\""
            },
            // Expression before (becomes) Expression (after)
            row("", "-"),
            row("3", "3-"),
            row("5-", "5-"), // doesn't apply double plus
            row("(", "(-"),
            row("2/", "2/-"),
            row("*", "*-"),
            row("%", "%-"),
            row("+", "+"),
            row("0.23-", "0.23-"),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOperator.Minus)

            res shouldBe expected
        }
    }

    "appending '*' to an expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" will become \"$expected\""
            },
            // Expression before (becomes) Expression (after)
            row("1", "1*"),
            row("232.99", "232.99*"),
            row(".5", ".5*"),
            row("1.", "1.*"),
            row("", ""),
            row("+", "+"),
            row("-", "-"),
            row("/", "/"),
            row("*", "*"),
            row("10%", "10%*"),
            row("(", "("),
            row(")", ")*"),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOperator.Multiply)

            res shouldBe expected
        }
    }
})