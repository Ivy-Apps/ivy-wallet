package com.ivy.frp

import com.ivy.frp.action.Action

//TODO: Implement properly

infix fun <A, B, C, D> ((A, B) -> C).then(f: (C) -> D): (A, B) -> D = { a, b ->
    val c = this(a, b)
    f(c)
}

infix fun <A, B, C, D> ((A, B) -> C).then(act: Action<C, D>): suspend (A, B) -> D = { a, b ->
    val c = this(a, b)
    act(c)
}

suspend infix fun <A, B, C, D> (suspend (A, B) -> C).then(f: suspend (C) -> D): suspend (A, B) -> D =
    { a, b ->
        val c = this(a, b)
        f(c)
    }

infix fun <A, B, C, D, E> ((A, B, C) -> D).then(f: (D) -> E): (A, B, C) -> E = { a, b, c ->
    val d = this(a, b, c)
    f(d)
}