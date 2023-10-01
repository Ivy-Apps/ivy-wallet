package com.ivy.domain.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.domain.exact.Exact

@JvmInline
value class IconId private constructor(val id: String) {
    companion object : Exact<String, IconId> {
        override val name = "IconId"

        override fun Raise<String>.spec(raw: String): IconId {
            val notBlankTrimmed = NotBlankTrimmedString.from(raw).bind().value
            ensure(" " !in notBlankTrimmed) { "Cannot contain spaces" }
            return IconId(notBlankTrimmed.lowercase())
        }

    }
}