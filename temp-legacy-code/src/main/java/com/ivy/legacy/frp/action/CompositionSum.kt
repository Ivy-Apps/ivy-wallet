package com.ivy.frp.action

import java.math.BigDecimal

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <A, B> (suspend (A) -> List<B>).thenSum(
    crossinline value: (B) -> BigDecimal
): suspend (A) -> BigDecimal =
    { a ->
        val list = this(a)
        list.fold(
            initial = BigDecimal.ZERO,
            operation = { acc, b ->
                acc + value(b)
            }
        )
    }

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <A, B> (Action<A, List<B>>).thenSum(
    crossinline value: (B) -> BigDecimal
): suspend (A) -> BigDecimal =
    { a ->
        val list = this(a)
        list.fold(
            initial = BigDecimal.ZERO,
            operation = { acc, b ->
                acc + value(b)
            }
        )
    }