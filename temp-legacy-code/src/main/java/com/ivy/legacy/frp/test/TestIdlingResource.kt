package com.ivy.frp.test

import java.util.concurrent.atomic.AtomicInteger

@Deprecated("Legacy code. Don't use it, please.")
object TestIdlingResource {
    private val counter = AtomicInteger(0)

    @Deprecated("Legacy code. Don't use it, please.")
    fun increment() {
    }

    @Deprecated("Legacy code. Don't use it, please.")
    fun decrement() {
    }

    @Deprecated("Legacy code. Don't use it, please.")
    fun reset() {
        counter.set(0)
    }

    @Deprecated("Legacy code. Don't use it, please.")
    fun get(): Int {
        return counter.get()
    }
}