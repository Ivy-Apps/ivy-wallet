package com.ivy.fp.action

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


suspend infix fun <A, B, C> (suspend (A) -> B).then(f: suspend (B) -> C): suspend (A) -> C =
    { a ->
        val b = this@then(a)
        f(b)
    }

suspend infix fun <B, C> (suspend () -> B).then(f: suspend (B) -> C): suspend () -> C =
    {
        val b = this@then()
        f(b)
    }

suspend infix fun <A, B, C> (suspend (A) -> B).then(act: Action<B, C>): suspend (A) -> C =
    { a ->
        val b = this@then(a)
        act(b)
    }

suspend infix fun <B, C> (suspend () -> B).then(act: Action<B, C>): suspend () -> C =
    {
        val b = this@then()
        act(b)
    }

suspend infix fun <A, B, C> (Action<A, B>).then(f: suspend (B) -> C): suspend (A) -> C =
    { a ->
        val b = this@then(a)
        f(b)
    }


suspend infix fun <B, C> (() -> B).then(f: suspend (B) -> C): suspend () -> C =
    {
        val b = this@then()
        f(b)
    }

suspend infix fun <B, C> (suspend () -> B).thenInvokeAfter(f: suspend (B) -> C): C {
    val b = this@thenInvokeAfter()
    return f(b)
}

fun <C> (() -> C).fixUnit(): suspend (Unit) -> C =
    {
        this()
    }

fun <C> (suspend () -> C).fixUnit(): suspend (Unit) -> C =
    {
        this()
    }

fun <C> (suspend (Unit) -> C).fixUnit(): suspend () -> C =
    {
        this(Unit)
    }

fun <A, B> (Action<A, B>).lambda(): suspend (A) -> B = { a ->
    this(a)
}

suspend inline fun <T, R> Iterable<T>.mapAsync(
    scope: CoroutineScope,
    crossinline transform: suspend (T) -> R
): List<R> {
    return this.map {
        scope.async {
            transform(it)
        }
    }.awaitAll()
}