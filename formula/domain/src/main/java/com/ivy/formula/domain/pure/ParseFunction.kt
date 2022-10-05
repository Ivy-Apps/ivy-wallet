package com.ivy.formula.domain.pure

import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow

/**
 * TODO: Implement recursive descent parsing
 */

fun parseFunction(
    function: String
): (Flow<NonEmptyList<Double>>) -> Flow<Double> = { args ->
    TODO()
}