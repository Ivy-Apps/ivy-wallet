package com.ivy.frp.monad

import com.ivy.frp.action.Action
import com.ivy.frp.asParamTo
import com.ivy.frp.thenInvokeAfter


sealed class Res<out E, out T> {
    data class Ok<out T>(val data: T) : Res<Nothing, T>()

    data class Err<out E>(val error: E) : Res<E, Nothing>()
}

inline fun <E, T, S> Res<E, T>.map(f: (Res<E, T>) -> S): S {
    return f(this)
}

inline fun <T> tryOp(
    noinline operation: suspend () -> T,
): suspend () -> Res<Exception, T> = {
    try {
        operation thenInvokeAfter { Res.Ok(it) }
    } catch (e: Exception) {
        Res.Err(e)
    }
}

inline fun <A, T> tryOpWithParam(
    crossinline operation: suspend (A) -> T,
): suspend (A) -> Res<Exception, T> = { a ->
    try {
        a asParamTo operation thenInvokeAfter { Res.Ok(it) }
    } catch (e: Exception) {
        Res.Err(e)
    }
}

// ------------------ mapError --------------------------------------
inline infix fun <A, E, T, E2> (suspend (A) -> Res<E, T>).mapError(
    crossinline errorMapping: suspend (E) -> E2
): suspend (A) -> Res<E2, T> = { a ->
    when (val res = this(a)) {
        is Res.Err<E> -> Res.Err(errorMapping(res.error))
        is Res.Ok<T> -> res
    }
}

inline infix fun <E, T, E2> (suspend () -> Res<E, T>).mapError(
    crossinline errorMapping: suspend (E) -> E2
): suspend () -> Res<E2, T> = {
    when (val res = this()) {
        is Res.Err<E> -> Res.Err(errorMapping(res.error))
        is Res.Ok<T> -> res
    }
}
// ------------------ mapError --------------------------------------


// ------------------ mapSuccess --------------------------------------
inline infix fun <A, E, T, T2> (suspend (A) -> Res<E, T>).mapSuccess(
    crossinline successMapping: suspend (T) -> T2
): suspend (A) -> Res<E, T2> = { a ->
    when (val res = this(a)) {
        is Res.Err<E> -> res
        is Res.Ok<T> -> Res.Ok(successMapping(res.data))
    }
}

inline infix fun <E, T, T2> (suspend () -> Res<E, T>).mapSuccess(
    crossinline successMapping: suspend (T) -> T2
): suspend () -> Res<E, T2> = {
    when (val res = this()) {
        is Res.Err<E> -> res
        is Res.Ok<T> -> Res.Ok(successMapping(res.data))
    }
}

infix fun <E, T, T2> (suspend () -> Res<E, T>).mapSuccess(
    successAct: Action<T, T2>
): suspend () -> Res<E, T2> = {
    when (val res = this()) {
        is Res.Err<E> -> res
        is Res.Ok<T> -> Res.Ok(successAct(res.data))
    }
}
// ------------------ mapSuccess --------------------------------------
