package com.ivy.frp.action

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <A, B, C> (suspend (A) -> List<B>).thenMap(
    crossinline transform: suspend (B) -> C
): suspend (A) -> List<C> =
    { a ->
        val list = this(a)
        list.map {
            transform(it)
        }
    }

@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <B, C> (suspend () -> List<B>).thenMap(
    crossinline transform: suspend (B) -> C
): suspend () -> List<C> =
    {
        val list = this()
        list.map {
            transform(it)
        }
    }

suspend inline infix fun <B, C> (suspend () -> List<B>).thenFlatMap(
    crossinline transform: suspend (B) -> List<C>
): suspend () -> List<C> =
    {
        val list = this()
        list.flatMap {
            transform(it)
        }
    }

suspend inline infix fun <B, C> (suspend () -> List<B>).thenMap(
    act: Action<B, C>
): suspend () -> List<C> =
    {
        val list = this()
        list.map {
            act(it)
        }
    }

suspend inline infix fun <A, B, C> (Action<A, List<B>>).thenMap(
    crossinline transform: suspend (B) -> C
): suspend (A) -> List<C> =
    { a ->
        val list = this(a)
        list.map {
            transform(it)
        }
    }
