package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.data.model.exact.Exact

@JvmInline
value class NonNegativeDouble private constructor(val value: Double) {
    companion object : Exact<Double, NonNegativeDouble> {
        override val exactName = "NonNegativeDouble"

        override fun Raise<String>.spec(raw: Double): NonNegativeDouble {
            ensure(raw >= 0.0) { "$raw is not >= 0" }
            ensure(raw.isFinite()) { "Is not a finite number" }
            return NonNegativeDouble(raw)
        }
    }
}

fun PositiveDouble.toNonNegative(): NonNegativeDouble = NonNegativeDouble.unsafe(value)
