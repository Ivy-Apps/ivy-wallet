package com.ivy.common

import arrow.core.NonEmptyList

fun <T> List<T>.toNonEmptyList(): NonEmptyList<T> = NonEmptyList.fromListUnsafe(this)