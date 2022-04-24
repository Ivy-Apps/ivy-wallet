package com.ivy.fp.action

suspend infix fun <A, B> (suspend (A) -> List<B>).thenFilter(
    predicate: (B) -> Boolean
): suspend (A) -> List<B> =
    { a ->
        val list = this(a)
        list.filter(predicate)
    }

suspend infix fun <A, B> (Action<A, List<B>>).thenFilter(
    predicate: (B) -> Boolean
): suspend (A) -> List<B> =
    { a ->
        val list = this(a)
        list.filter(predicate)
    }


suspend infix fun <B> (suspend () -> List<B>).thenFilter(
    predicate: suspend (B) -> Boolean
): suspend () -> List<B> =
    {
        val list = this()
        list.filter {
            predicate(it)
        }
    }
