package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.data.model.exact.Exact

@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object : Exact<Int, PositiveInt> {
        override val exactName = "PositiveInt"

        override fun Raise<String>.spec(raw: Int): PositiveInt {
            ensure(raw > 0) { "$raw is not > 0" }
            return PositiveInt(raw)
        }
    }
}
