package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.data.model.exact.Exact

@JvmInline
value class NonZeroDouble private constructor(val value: Double) {
    companion object : Exact<Double, NonZeroDouble> {
        override val exactName = "NonZeroDouble"

        override fun Raise<String>.spec(raw: Double): NonZeroDouble {
            ensure(raw != 0.0) { "$raw is zero! It should be non-zero" }
            ensure(raw.isFinite()) { "Is not a finite number" }
            return NonZeroDouble(raw)
        }
    }
}
