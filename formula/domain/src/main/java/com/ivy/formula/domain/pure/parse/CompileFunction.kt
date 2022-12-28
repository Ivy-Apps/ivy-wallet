package com.ivy.formula.domain.pure.parse

import arrow.core.Either
import arrow.core.NonEmptyList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(FlowPreview::class)
fun compileFunction(
    function: String
): (Flow<NonEmptyList<Double>>) -> Flow<Double> = { argsFlow ->
    argsFlow.flatMapLatest { args ->
        val parser = FunctionParser(args)
        when (val result = parser.parse(normalizeFunction(function))) {
            is Either.Left -> error(result.value)
            is Either.Right -> flowOf(result.value)
        }
    }
}

private fun normalizeFunction(function: String): String =
    function.replace("=", "")
        .replace(" ", "")
        .trim()