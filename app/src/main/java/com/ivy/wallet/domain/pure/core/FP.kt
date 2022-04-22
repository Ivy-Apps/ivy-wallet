package com.ivy.wallet.domain.pure.core

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

infix fun <A, B, C> ((A) -> B).compose(fn2: (B) -> C): (A) -> C = { a ->
    val b = this(a)
    val c = fn2(b)
    c
}

infix fun <A, B, C> ((B) -> C).after(fn1: (A) -> B): (A) -> C = { a ->
    val b = fn1(a)
    val c = this(b)
    c
}