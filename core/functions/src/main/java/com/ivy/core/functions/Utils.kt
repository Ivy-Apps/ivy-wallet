package com.ivy.core.functions

import arrow.core.NonEmptyList


suspend fun <T> NonEmptyList<T>.mapIndexedNel(
    f: suspend (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed { index, value ->
            f(index, value)
        }
    )
}

fun nonEmptyListOfZeros(n: Int): NonEmptyList<Double> {
    return NonEmptyList.fromListUnsafe(
        List(n) { 0.0 }
    )
}