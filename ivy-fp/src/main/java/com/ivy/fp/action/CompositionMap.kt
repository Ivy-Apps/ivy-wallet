package com.ivy.fp.action

suspend infix fun <A, B, C> (suspend (A) -> List<B>).thenMap(
    transform: suspend (B) -> C
): suspend (A) -> List<C> =
    { a ->
        val list = this(a)
        list.map {
            transform(it)
        }
    }

suspend infix fun <B, C> (suspend () -> List<B>).thenMap(
    transform: suspend (B) -> C
): suspend () -> List<C> =
    {
        val list = this()
        list.map {
            transform(it)
        }
    }

suspend infix fun <A, B, C> (Action<A, List<B>>).thenMap(
    transform: suspend (B) -> C
): suspend (A) -> List<C> =
    { a ->
        val list = this(a)
        list.map {
            transform(it)
        }
    }
