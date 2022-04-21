package com.ivy.wallet.domain.action

suspend infix fun <A> (suspend (Any) -> List<A>).thenFilter(
    predicate: (A) -> Boolean
): suspend (Any) -> List<A> =
    { a ->
        val list = this(a)
        list.filter(predicate)
    }
