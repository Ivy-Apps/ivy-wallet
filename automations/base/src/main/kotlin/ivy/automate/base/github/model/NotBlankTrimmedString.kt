package ivy.automate.base.github.model

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class NotBlankTrimmedString private constructor(val value: String) {
    companion object : Exact<String, NotBlankTrimmedString> {
        override val exactName: String = "NotBlankTrimmedString"

        override fun Raise<String>.spec(raw: String): NotBlankTrimmedString {
            ensure(raw.isNotBlank()) { "Cannot be blank " }
            return NotBlankTrimmedString(raw.trim())
        }
    }
}