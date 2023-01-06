package com.ivy.formula.domain.pure.parse

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.computations.either
import arrow.core.left

data class FunctionParser(val args: NonEmptyList<Double>) {

    @Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
    suspend fun parse(function: String): Either<String, Double> = either {
        val result = expr().invoke(function)
        if (result.isEmpty()) "Parse error.".left().bind()
        if (result.size > 1) "Ambiguous result.".left().bind()
        val leftover = result.first().leftover
        if (leftover.isNotEmpty())
            "Not completely parsed, leftover: \"$leftover\".".left().bind()

        Either.Right(result.first().value).bind()
    }

    private fun expr(): Parser<Double> = term().flatMap { x ->
        char('+').flatMap {
            expr().flatMap { y ->
                pure(x + y)
            }
        }
    } or term()

    private fun term(): Parser<Double> = factor().flatMap { x ->
        char('%').flatMap {
            pure(x / 100)
        }
    } or factor().flatMap { x ->
        char('*').flatMap {
            term().flatMap { y ->
                pure(x * y)
            }
        }
    } or factor().flatMap { x ->
        char('/').flatMap {
            term().flatMap { y ->
                pure(x / y)
            }
        }
    } or factor()

    private fun factor(): Parser<Double> = char('(').flatMap {
        expr().flatMap { x ->
            char(')').flatMap {
                pure(x)
            }
        }
    } or argument()

    private fun argument(): Parser<Double> = char('$').flatMap {
        sat { it.isDigit() }.flatMap { digit ->
            val index = digit.digitToInt() - 1
            pure(args[index])
        }
    }
}
