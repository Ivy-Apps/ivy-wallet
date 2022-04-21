package com.ivy.wallet.domain.action

import java.math.BigDecimal

suspend infix fun <A, B> (suspend (A) -> List<B>).thenSum(
    value: (B) -> BigDecimal
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

suspend infix fun <A, B> (Action<A, List<B>>).thenSum(
    value: (B) -> BigDecimal
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