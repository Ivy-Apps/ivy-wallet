package com.ivy.fp.monad

import com.ivy.fp.action.Action

//suspend infix fun <A, Result<B>, C> (Action<A, B>).thenR(act: Action<>): suspend (A) -> C =
//    { a ->
//        val b = this@then(a)
//        f(b)
//    }

//Combinations between Act, suspend fun()


//Action -> Action
suspend infix fun <A, T1, E, T2> (Action<A, Res<E, T1>>).thenR(
    act: Action<T1, Res<E, T2>>
): suspend (A) -> Res<E, T2> = { a ->
    when (val res1 = this@thenR(a)) {
        is Res.Err<E, *> -> res1
        is Res.Ok<*, T1> -> act(res1.data)
    }
}

//Action -> Suspend fun
suspend infix fun <A, T1, E, T2> (Action<A, Res<E, T1>>).thenR(
    f: suspend (T1) -> Res<E, T2>
): suspend (A) -> Res<E, T2> = { a ->
    when (val res1 = this@thenR(a)) {
        is Res.Err<E, *> -> res1
        is Res.Ok<*, T1> -> f(res1.data)
    }
}

//Suspend fun -> Action
suspend infix fun <A, T1, E, T2> (suspend (A) -> Res<E, T1>).thenR(
    act: Action<T1, Res<E, T2>>
): suspend (A) -> Res<E, T2> = { a ->
    when (val res1 = this@thenR(a)) {
        is Res.Err<E, *> -> res1
        is Res.Ok<*, T1> -> act(res1.data)
    }
}

//Suspend fun -> Suspend fund
suspend infix fun <A, T1, E, T2> (suspend (A) -> Res<E, T1>).thenR(
    f: suspend (T1) -> Res<E, T2>
): suspend (A) -> Res<E, T2> = { a ->
    when (val res1 = this@thenR(a)) {
        is Res.Err<E, *> -> res1
        is Res.Ok<*, T1> -> f(res1.data)
    }
}