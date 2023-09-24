package com.ivy.frp

import com.ivy.frp.action.Action

//Cases:
//A
//() -> B
//(A) -> B
//suspend () -> B
//suspend (A) -> B
//Action<A,B>

//Eligible 2nd position
//(A) -> B
//suspend (A) -> B
//Action<A,B>

//~ 18 possible combinations
//Note: Non-suspend variants are skipped because or ambiguity errors

// -------------------------- A ------------------------------------
//A => (A) -> B
//inline infix fun <A, B> A.asParamTo(crossinline f: (A) -> B): () -> B = {
//    f(this)
//}

//A => suspend (A) -> B
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B> A.asParamTo(crossinline f: suspend (A) -> B): suspend () -> B = {
    f(this)
}

//A => Action<A,B>
@Deprecated("Legacy code. Don't use it, please.")
infix fun <A, B> A.asParamTo(act: Action<A, B>): suspend () -> B = {
    act(this)
}
// -------------------------- A ------------------------------------

// -------------------------- () -> B ------------------------------------
////() -> B => (B) -> C
//inline infix fun <B, C> (() -> B).then(crossinline f: (B) -> C): () -> C = {
//    val b = this()
//    f(b)
//}
//Error: Ambiguity

//() -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <B, C> (() -> B).then(crossinline f: suspend (B) -> C): suspend () -> C = {
    val b = this()
    f(b)
}

//() -> B => Action<A,B>
@Deprecated("Legacy code. Don't use it, please.")
infix fun <B, C> (() -> B).then(act: Action<B, C>): suspend () -> C = {
    val b = this()
    act(b)
}
// -------------------------- () -> B ------------------------------------

// -------------------------- (A) -> C ------------------------------------
//(A) -> B => (B) -> C
//inline infix fun <A, B, C> ((A) -> B).then(crossinline f: (B) -> C): (A) -> C = { a ->
//    val b = this(a)
//    f(b)
//}
//ERROR: Ambiguity

//(A) -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B, C> ((A) -> B).then(crossinline f: suspend (B) -> C): suspend (A) -> C =
    { a ->
        val b = this(a)
        f(b)
    }

//(A) -> B => Action<B,C>
//infix fun <A, B, C> ((A) -> B).then(act: Action<B, C>): suspend (A) -> C = { a ->
//    val b = this(a)
//    act(b)
//}
//ERROR: Ambiguity
// -------------------------- (A) -> C ------------------------------------

// -------------------------- suspend () -> B ------------------------------------
//suspend () -> B => (B) -> C
//infix fun <B, C> (suspend () -> B).then(f: (B) -> C): suspend () -> C = {
//    val b = this()
//    f(b)
//}
//Same as: infix fun <A, B, C> ((A) -> B).then(f: (B) -> C): (A) -> C

//suspend () -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <B, C> (suspend () -> B).then(crossinline f: suspend (B) -> C): suspend () -> C = {
    val b = this()
    f(b)
}

//suspend () -> B => Action<A,B>
@Deprecated("Legacy code. Don't use it, please.")
infix fun <B, C> (suspend () -> B).then(act: Action<B, C>): suspend () -> C = {
    val b = this()
    act(b)
}
// -------------------------- suspend () -> B ------------------------------------

// -------------------------- suspend (A) -> B ------------------------------------
//suspend (A) -> B => (B) -> C
//inline infix fun <A, B, C> (suspend (A) -> B).then(crossinline f: (B) -> C): suspend (A) -> C =
//    { a ->
//        val b = this(a)
//        f(b)
//    }
// ERROR: Ambiguity

//suspend (A) -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B, C> (suspend (A) -> B).then(
    crossinline f: suspend (B) -> C
): suspend (A) -> C =
    { a ->
        val b = this(a)
        f(b)
    }

//(A) -> B => Action<B,C>
@Deprecated("Legacy code. Don't use it, please.")
infix fun <A, B, C> (suspend (A) -> B).then(act: Action<B, C>): suspend (A) -> C = { a ->
    val b = this(a)
    act(b)
}
// -------------------------- suspend (A) -> B ------------------------------------

// -------------------------- Action<A,B> ------------------------------------
//Action<A,B> => (B) -> C
//inline infix fun <A, B, C> (Action<A, B>).then(crossinline f: (B) -> C): suspend (A) -> C = { a ->
//    val b = this(a)
//    f(b)
//}
//ERROR: Ambiguity

//Action<A,B> => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
inline infix fun <A, B, C> (Action<A, B>).then(crossinline f: suspend (B) -> C): suspend (A) -> C =
    { a ->
        val b = this(a)
        f(b)
    }

//Action<A,B> => Action<B,C>
@Deprecated("Legacy code. Don't use it, please.")
infix fun <A, B, C> (Action<A, B>).then(act: Action<B, C>): suspend (A) -> C = { a ->
    val b = this(a)
    act(b)
}
// -------------------------- Action<A,B> ------------------------------------

//================================= thenInvokeAfter ==========================================
//First part:
//() -> B
//(A) -> B
//suspend () -> B
//suspend (A) -> B
//Action<A,B>

//Second part:
//(A) -> B
//suspend (A) -> B
//Action<A,B>

//--------------------------- () -> B -----------------------------
//() -> B => (B) -> C
//inline infix fun <B, C> (() -> B).thenInvokeAfter(crossinline f: (B) -> C): C {
//    val b = this@thenInvokeAfter()
//    return f(b)
//}
//ERROR: Ambiguity

//() -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <B, C> (() -> B).thenInvokeAfter(crossinline f: suspend (B) -> C): C {
    val b = this@thenInvokeAfter()
    return f(b)
}

//() -> B => Action<B,C>
@Deprecated("Legacy code. Don't use it, please.")
suspend infix fun <B, C> (() -> B).thenInvokeAfter(act: Action<B, C>): C {
    val b = this@thenInvokeAfter()
    return act(b)
}
//--------------------------- () -> B -----------------------------

//--------------------------- (A) -> B -----------------------------
//(A) -> B => (B) -> C
//infix fun <A, B, C> ((A) -> B).thenInvokeAfter(f: (B) -> C): C { "a ->" -- cannot have this
//    val b = this@thenInvokeAfter()
//    return f(b)
//}
//INVALID BECAUSE "A" parameter will turn the function into lambda

//(A) -> B => suspend (B) -> C
//(A) -> B => Action<B,C>
//--------------------------- (A) -> B -----------------------------

//--------------------------- suspend () -> B -----------------------------
//suspend () -> B => (B) -> C
//suspend infix fun <B, C> (suspend () -> B).thenInvokeAfter(f: (B) -> C): C {
//    val b = this@thenInvokeAfter()
//    return f(b)
//}

//() -> B => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <B, C> (suspend () -> B).thenInvokeAfter(
    crossinline f: suspend (B) -> C
): C {
    val b = this@thenInvokeAfter()
    return f(b)
}

//suspend () -> B => Action<B,C>
@Deprecated("Legacy code. Don't use it, please.")
suspend infix fun <B, C> (suspend () -> B).thenInvokeAfter(act: Action<B, C>): C {
    val b = this@thenInvokeAfter()
    return act(b)
}
//--------------------------- suspend () -> B -----------------------------

//--------------------------- suspend (A) -> B -----------------------------
//INVALID BECAUSE NO "A"
//suspend (A) -> B => (B) -> C
//suspend(A) -> B => suspend (B) -> C
//suspend (A) -> B => Action<B,C>
//--------------------------- suspend (A) -> B -----------------------------

//--------------------------- Action<Unit,B> -----------------------------
//Action<Unit,B> => (B) -> C
//Error: Ambiguity

//Action<Unit,B> => suspend (B) -> C
@Deprecated("Legacy code. Don't use it, please.")
suspend inline infix fun <B, C> (Action<Unit, B>).thenInvokeAfter(
    crossinline f: suspend (B) -> C
): C {
    val b = this@thenInvokeAfter(Unit)
    return f(b)
}

//Action<A,B> => Action<B,C>
@Deprecated("Legacy code. Don't use it, please.")
suspend infix fun <B, C> (Action<Unit, B>).thenInvokeAfter(act: Action<B, C>): C {
    val b = this@thenInvokeAfter(Unit)
    return act(b)
}
//--------------------------- Action<A,B> -----------------------------

//===============================  thenInvokeAfter =============================================


// ---------------------------------- .fixUnit() -------------------------------------------
@Deprecated("Legacy code. Don't use it, please.")
fun <C> (() -> C).fixUnit(): (Unit) -> C = {
    this()
}

@Deprecated("Legacy code. Don't use it, please.")
fun <C> (suspend () -> C).fixUnit(): suspend (Unit) -> C = {
    this()
}

@Deprecated("Legacy code. Don't use it, please.")
fun <C> ((Unit) -> C).fixUnit(): () -> C = {
    this(Unit)
}

@Deprecated("Legacy code. Don't use it, please.")
fun <C> (suspend (Unit) -> C).fixUnit(): suspend () -> C = {
    this(Unit)
}
// ---------------------------------- .fixUnit() -------------------------------------------

@Deprecated("Legacy code. Don't use it, please.")
fun <A, B> (Action<A, B>).lambda(): suspend (A) -> B = { a ->
    this(a)
}

@Deprecated("Legacy code. Don't use it, please.")
fun <B> (Action<Unit, B>).lambda(): suspend () -> B = {
    this(Unit)
}

@Deprecated("Legacy code. Don't use it, please.")
fun <A> (A).lambda(): suspend () -> A = suspend {
    this
}