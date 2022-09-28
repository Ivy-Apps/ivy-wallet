package com.ivy.core.domain.test

import androidx.compose.ui.test.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

object TestIdlingResource {
    private val counter = AtomicInteger(0)

    @JvmField
    val idlingResource = object : IdlingResource {
        override val isIdleNow: Boolean
            get() = counter.get() == 0
    }

    fun increment() {
        counter.incrementAndGet()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            counter.decrementAndGet()
        }
    }

    fun reset() {
        counter.set(0)
    }

    fun get(): Int {
        return counter.get()
    }
}