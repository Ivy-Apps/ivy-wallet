package com.ivy.fp.monad

import com.ivy.fp.action.then


sealed class Res<out E, out T> {
    data class Ok<out T>(val data: T) : Res<Nothing, T>()

    data class Err<out E>(val error: E) : Res<E, Nothing>()
}

fun <E, T, S> Res<E, T>.map(f: (Res<E, T>) -> S): S {
    return f(this)
}

suspend fun <E, T, T2> tryOp(
    operation: suspend () -> T,
    mapError: suspend (Exception) -> E,
    mapSuccess: suspend (T) -> T2
): suspend () -> Res<E, T2> = {
    try {
        (operation then mapSuccess then { Res.Ok(it) }).invoke()
    } catch (e: Exception) {
        ({ e } then mapError then { Res.Err(it) }).invoke()
    }
}

suspend fun <T> tryOp(
    operation: suspend () -> T,
): suspend () -> Res<Exception, T> = {
    try {
        (operation then { Res.Ok(it) }).invoke()
    } catch (e: Exception) {
        Res.Err(e)
    }
}

// ------------------ mapError --------------------------------------
suspend infix fun <A, E, T, E2> (suspend (A) -> Res<E, T>).mapError(
    errorMapping: suspend (E) -> E2
): suspend (A) -> Res<E2, T> = { a ->
    when (val res = this(a)) {
        is Res.Err<E> -> Res.Err(errorMapping(res.error))
        is Res.Ok<T> -> res
    }
}

suspend infix fun <E, T, E2> (suspend () -> Res<E, T>).mapError(
    errorMapping: suspend (E) -> E2
): suspend () -> Res<E2, T> = {
    when (val res = this()) {
        is Res.Err<E> -> Res.Err(errorMapping(res.error))
        is Res.Ok<T> -> res
    }
}
// ------------------ mapError --------------------------------------


// ------------------ mapSuccess --------------------------------------
suspend infix fun <A, E, T, T2> (suspend (A) -> Res<E, T>).mapSuccess(
    successMapping: suspend (T) -> T2
): suspend (A) -> Res<E, T2> = { a ->
    when (val res = this(a)) {
        is Res.Err<E> -> res
        is Res.Ok<T> -> Res.Ok(successMapping(res.data))
    }
}

suspend infix fun <E, T, T2> (suspend () -> Res<E, T>).mapSuccess(
    successMapping: suspend (T) -> T2
): suspend () -> Res<E, T2> = {
    when (val res = this()) {
        is Res.Err<E> -> res
        is Res.Ok<T> -> Res.Ok(successMapping(res.data))
    }
}
// ------------------ mapSuccess --------------------------------------
