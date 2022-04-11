package com.ivy.wallet.domain.fp.core

import arrow.core.NonEmptyList
import java.math.BigDecimal

fun <T> NonEmptyList<T>.mapIndexedNel(
    f: (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed(f)
    )
}

suspend fun <T> NonEmptyList<T>.mapIndexedNelSuspend(
    f: suspend (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed { index, value ->
            f(index, value)
        }
    )
}

fun nonEmptyListOfZeros(n: Int): NonEmptyList<BigDecimal> {
    return NonEmptyList.fromListUnsafe(
        List(n) { BigDecimal.ZERO }
    )
}