package com.ivy.frp.action

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <A, B> (suspend (A) -> List<B>).thenFilter(
    crossinline predicate: (B) -> Boolean
): suspend (A) -> List<B> =
    { a ->
        val list = this(a)
        list.filter(predicate)
    }

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <A, B> (Action<A, List<B>>).thenFilter(
    crossinline predicate: (B) -> Boolean
): suspend (A) -> List<B> =
    { a ->
        val list = this(a)
        list.filter(predicate)
    }

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <B> (suspend () -> List<B>).thenFilter(
    crossinline predicate: suspend (B) -> Boolean
): suspend () -> List<B> =
    {
        val list = this()
        list.filter {
            predicate(it)
        }
    }
