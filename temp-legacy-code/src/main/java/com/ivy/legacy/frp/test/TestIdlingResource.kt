package com.ivy.frp.test

import java.util.concurrent.atomic.AtomicInteger

object TestIdlingResource {
    private val counter = AtomicInteger(0)


    fun increment() {
    }

    fun decrement() {
    }

    fun reset() {
        counter.set(0)
    }

    fun get(): Int {
        return counter.get()
    }
}