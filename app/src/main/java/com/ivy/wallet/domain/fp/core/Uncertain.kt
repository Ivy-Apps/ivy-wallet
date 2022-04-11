package com.ivy.wallet.domain.fp.core

data class Uncertain<E : List<*>, V>(
    val error: E,
    val value: V
) {
    fun isCertain(): Boolean {
        return error.isEmpty()
    }
}