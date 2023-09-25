package com.ivy.frp

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

/**
 * Non suspend function composition. !EXPERIMENTAL!
 */

//Cases:
//A
//() -> B
//(A) -> B

//Eligible 2nd position
//(A) -> B

// --------- A (asParamTo2) -------------------------
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B> A.asParamTo2(crossinline f: (A) -> B): () -> B = {
    f(this)
}
// --------- A (asParamTo2) -------------------------

//() -> B => (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <B, C> (() -> B).then2(crossinline f: (B) -> C): () -> C = {
    f(this())
}

//(A) -> B => (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B, C> ((A) -> B).then2(crossinline f: (B) -> C): (A) -> C = { a ->
    val b = this(a)
    f(b)
}


@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B, C> ((A) -> Option<B>).thenMaybe2(crossinline f: (B) -> C): (A) -> Option<C> =
    { a ->
        when (val b = this(a)) {
            None -> None
            is Some -> Some(f(b.value))
        }
    }

@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A> ((A) -> Option<Unit>).fixUnit2(crossinline f: () -> Unit): (A) -> Unit =
    { _ -> }

@Deprecated("Legacy code. Don't use it, please.")
fun <T> forward(): (T) -> T = { v: T -> v }
