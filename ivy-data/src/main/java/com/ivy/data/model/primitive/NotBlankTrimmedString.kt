package com.ivy.data.model.primitive

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.ivy.base.exact.Exact

@JvmInline
value class NotBlankTrimmedString private constructor(val value: String) {
    companion object : Exact<String, NotBlankTrimmedString> {
        override val exactName = "NotBlankTrimmedString"

        override fun Raise<String>.spec(raw: String): NotBlankTrimmedString {
            val trimmed = raw.trim()
            ensure(trimmed.isNotBlank()) { "Cannot be blank" }
            return NotBlankTrimmedString(trimmed)
        }
    }
}