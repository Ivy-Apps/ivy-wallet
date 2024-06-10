package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.data.model.exact.Exact

@JvmInline
value class NonNegativeLong private constructor(val value: Long) {
    companion object : Exact<Long, NonNegativeLong> {
        override val exactName = "NonNegativeLong"

        override fun Raise<String>.spec(raw: Long): NonNegativeLong {
            ensure(raw >= 0) { "$raw is not >= 0" }
            return NonNegativeLong(raw)
        }

        val Zero = NonNegativeLong.unsafe(0L)
    }
}

fun Long.toNonNegative(): NonNegativeLong = NonNegativeLong.unsafe(this)
