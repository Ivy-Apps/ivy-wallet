package com.ivy.fp

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Pure

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Total

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Partial(val inCaseOf: String = "")


@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class SideEffect

infix fun <A, B, C> ((A) -> B).then(f: (B) -> C): (A) -> C = { a ->
    val b = this(a)
    f(b)
}

infix fun <A, B, C> ((B) -> C).after(fn1: (A) -> B): (A) -> C = { a ->
    val b = fn1(a)
    this(b)
}

infix fun <A, B, C, D> ((A, B) -> C).then(f: (C) -> D): (A, B) -> D = { a, b ->
    val c = this(a, b)
    f(c)
}

infix fun <A, B, C, D, E> ((A, B, C) -> D).then(f: (D) -> E): (A, B, C) -> E = { a, b, c ->
    val d = this(a, b, c)
    f(d)
}