package com.ivy.wallet.functional.core

import arrow.core.NonEmptyList
import java.math.BigDecimal

fun <T> NonEmptyList<T>.mapIndexedNel(
    f: (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed(f)
    )
}

fun nonEmptyListOfZeros(n: Int): NonEmptyList<BigDecimal> {
    return NonEmptyList.fromListUnsafe(
        List(n) { BigDecimal.ZERO }
    )
}