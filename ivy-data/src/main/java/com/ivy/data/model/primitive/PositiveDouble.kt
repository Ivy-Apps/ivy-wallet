package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.base.exact.Exact

@JvmInline
value class PositiveDouble private constructor(val value: Double) {
    companion object : Exact<Double, PositiveDouble> {
        override val exactName = "PositiveDouble"

        override fun Raise<String>.spec(raw: Double): PositiveDouble {
            ensure(raw > 0.0) {
                "$raw is not >= 0"
            }
            ensure(raw.isFinite()) {
                "Is not a finite number"
            }
            return PositiveDouble(raw)
        }

    }
}