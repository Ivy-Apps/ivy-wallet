package com.ivy.common

fun <T, R> T.map(f: (T) -> R): R = f(this)