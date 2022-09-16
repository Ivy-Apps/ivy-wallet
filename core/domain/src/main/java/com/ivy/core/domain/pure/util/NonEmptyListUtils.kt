package com.ivy.core.domain.pure.util

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

fun nonEmptyListOfZeros(n: Int): NonEmptyList<Double> =
    NonEmptyList.fromListUnsafe(List(n) { 0.0 })