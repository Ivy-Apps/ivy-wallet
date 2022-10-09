package com.ivy.math.calculator

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AppendCalculatorOperatorTest : FreeSpec({
    "appending '+' to an expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" becomes \"$expected\""
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
                "\"$expression\" becomes \"$expected\""
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
                "\"$expression\" becomes \"$expected\""
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

    "appending '/' to an expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" becomes \"$expected\""
            },
            // Expression before (becomes) Expression (after)
            row("1", "1/"),
            row("3.14", "3.14/"),
            row(".5", ".5/"),
            row("1.", "1./"),
            row("", ""),
            row("+", "+"),
            row("-", "-"),
            row("/", "/"),
            row("*", "*"),
            row("10%", "10%/"),
            row("(", "("),
            row(")", ")/"),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOperator.Divide)

            res shouldBe expected
        }
    }

    "appending brackets '()' to an expression" - {
        withData(
            nameFn = { (expression, expected) ->
                "\"$expression\" becomes \"$expected\""
            },
            // Expression before (becomes) Expression (after)
            row("", "("),
            row("(", "(("),
            row("-((", "-((("),
            row("(3", "(3)"),
            row("-(-25.0+10", "-(-25.0+10)"),
            row("1000/(300*3", "1000/(300*3)"),
            row("(3+", "(3+("),
            row("+(.25", "+(.25)"),
            row("((10+10)*33", "((10+10)*33)"),
            row("3", "3*("),
            row("2/", "2/("),
            row("(2+2)", "(2+2)*("),
            row("((50/5", "((50/5)"),
            row("((50/5)", "((50/5))"),
            row("((-", "((-("),
            row("10+10", "10+10*("),
            row("0.5", "0.5*("),
        ) { (expression, expected) ->
            val res = appendTo(expression, CalculatorOperator.Brackets)

            res shouldBe expected
        }
    }
})