package com.ivy.wallet.domain.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Action<in I, out O> {
    abstract suspend fun I.willDo(): O

    suspend operator fun invoke(input: I): O {
        return input.willDo()
    }

    protected suspend fun <T> io(action: suspend () -> T): T = withContext(Dispatchers.IO) {
        return@withContext action()
    }

    protected suspend fun <T> computation(action: suspend () -> T): T =
        withContext(Dispatchers.Default) {
            return@withContext action()
        }

    protected suspend fun <T> ui(action: suspend () -> T): T =
        withContext(Dispatchers.Main) {
            return@withContext action()
        }
}

infix fun <A, B, C> Action<B, C>.after(act1: Action<A, B>): Action<A, C> = object : Action<A, C>() {
    override suspend fun A.willDo(): C {
        val b = act1(this@willDo) //A -> B
        return this@after(b) //B -> C
    }
}

infix fun <A, B, C> Action<A, B>.then(act2: Action<B, C>): Action<A, C> = object : Action<A, C>() {
    override suspend fun A.willDo(): C {
        val b = this@then(this)
        return act2(b)
    }
}

suspend infix fun <A, B, C> Action<B, C>.after(lambda: suspend (A) -> B): suspend (A) -> C = { a ->
    val b = lambda(a)
    this@after(b)
}

suspend infix fun <A, B, C> (suspend (B) -> C).after(lambda: suspend (A) -> B): suspend (A) -> C =
    { a ->
        val b = lambda(a)
        this@after(b)
    }

suspend infix fun <A, B, C> (suspend (A) -> B).then(lambda: suspend (B) -> C): suspend (A) -> C =
    { a ->
        val b = this@then(a)
        lambda(b)
    }

suspend infix fun <B, C> (suspend () -> B).then(lambda: suspend (B) -> C): suspend () -> C =
    {
        val b = this@then()
        lambda(b)
    }

suspend infix fun <B, C> (suspend () -> B).then(act: Action<B, C>): suspend () -> C =
    {
        val b = this@then()
        act(b)
    }

suspend infix fun <A, B, C> (suspend (A) -> B).then(act: Action<B, C>): suspend (A) -> C =
    { a ->
        val b = this@then(a)
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