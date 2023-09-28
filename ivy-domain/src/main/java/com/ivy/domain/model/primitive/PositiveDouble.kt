package com.ivy.domain.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.domain.exact.Exact


@JvmInline
value class PositiveDouble private constructor(val value: Double) {
    companion object : Exact<Double, PositiveDouble> {
        override fun Raise<String>.spec(raw: Double): PositiveDouble {
            ensure(raw > 0.0) {
                "PositiveDouble: number '$raw' is not >= 0."
            }
            ensure(raw.isFinite()) {
                "PositiveDouble: must be a finite number."
            }
            return PositiveDouble(raw)
        }

    }
}