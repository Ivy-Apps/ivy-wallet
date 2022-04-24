package com.ivy.wallet.domain.pure.util

import arrow.core.NonEmptyList
import arrow.core.Option
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

fun Option<BigDecimal>.orZero(): BigDecimal {
    return this.orNull() ?: BigDecimal.ZERO
}