package com.ivy.wallet.functional.core

import arrow.core.NonEmptyList

fun <T> NonEmptyList<T>.mapIndexedNel(
    f: (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed(f)
    )
}